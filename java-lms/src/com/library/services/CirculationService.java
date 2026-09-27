package com.library.services;

import com.library.enums.BookStatus;
import com.library.enums.Role;
import com.library.models.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CirculationService {
    private final CatalogService catalogService;
    private final UserService userService;
    private final FineService fineService;
    private final AuditService auditService;

    private final Map<Integer, BorrowRecord> recordsById = new ConcurrentHashMap<>();
    private final Map<Role, BorrowPolicy> policies = new ConcurrentHashMap<>();
    private final AtomicInteger nextRecordId = new AtomicInteger(1);

    private int todayIssuesCount = 23;  // Seed baseline
    private int todayReturnsCount = 17; // Seed baseline

    public CirculationService(CatalogService catalogService, UserService userService,
                              FineService fineService, AuditService auditService) {
        this.catalogService = catalogService;
        this.userService = userService;
        this.fineService = fineService;
        this.auditService = auditService;

        initPolicies();
        seedInitialLoans();
    }

    private void initPolicies() {
        policies.put(Role.STUDENT, new BorrowPolicy(Role.STUDENT, 4, 14, 2, 2.0));
        policies.put(Role.FACULTY, new BorrowPolicy(Role.FACULTY, 8, 30, 3, 1.0));
        policies.put(Role.LIBRARIAN, new BorrowPolicy(Role.LIBRARIAN, 10, 30, 4, 0.0));
        policies.put(Role.ADMIN, new BorrowPolicy(Role.ADMIN, 15, 60, 5, 0.0));
    }

    public BorrowPolicy getPolicy(Role role) {
        return policies.getOrDefault(role, policies.get(Role.STUDENT));
    }

    public void updatePolicy(Role role, int maxBooks, int loanPeriodDays, int maxRenewals, double dailyFineRate) {
        policies.put(role, new BorrowPolicy(role, maxBooks, loanPeriodDays, maxRenewals, dailyFineRate));
        auditService.logAction("Librarian #001", "RULE_UPDATED",
                String.format("Updated %s policy: %d books, %d days, rate ₹%.2f", role.getDisplayName(), maxBooks, loanPeriodDays, dailyFineRate));
    }

    public synchronized BorrowRecord issueBook(String studentId, String copyBarcode, String actor) {
        Optional<Student> studentOpt = userService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        Student student = studentOpt.get();

        BorrowPolicy policy = getPolicy(student.getRole());
        if (student.getCurrentBorrowedCount() >= policy.getMaxBooks()) {
            throw new IllegalStateException("Student has reached maximum borrow limit of " + policy.getMaxBooks() + " books.");
        }

        if (student.getOutstandingFine() > 100.0) {
            throw new IllegalStateException("Outstanding fines (₹" + student.getOutstandingFine() + ") exceed ₹100 limit. Please clear dues first.");
        }

        Optional<BookCopy> copyOpt = catalogService.getCopyByBarcode(copyBarcode);
        if (copyOpt.isEmpty()) {
            throw new IllegalArgumentException("Book copy with barcode " + copyBarcode + " does not exist.");
        }
        BookCopy copy = copyOpt.get();

        if (!copy.isAvailable()) {
            throw new IllegalStateException("Book copy " + copyBarcode + " is currently " + copy.getStatus().getDescription());
        }

        Optional<Book> bookOpt = catalogService.getBookById(copy.getBookId());
        String bookTitle = bookOpt.map(Book::getTitle).orElse("Unknown Book");

        // Issue copy
        catalogService.updateCopyStatus(copyBarcode, BookStatus.ISSUED, student.getId());
        student.incrementBorrowed();

        int recordId = nextRecordId.getAndIncrement();
        BorrowRecord record = new BorrowRecord(recordId, student.getId(), copy.getBookId(),
                bookTitle, copyBarcode, LocalDate.now(), policy.getLoanPeriodDays());
        recordsById.put(recordId, record);

        todayIssuesCount++;

        auditService.logAction(actor != null ? actor : "Librarian #102", "BOOK_ISSUED",
                String.format("Issued '%s' [%s] to %s (%s)", bookTitle, copyBarcode, student.getName(), student.getId()));

        return record;
    }

    public synchronized BorrowRecord returnBook(String copyBarcode, String actor) {
        Optional<BookCopy> copyOpt = catalogService.getCopyByBarcode(copyBarcode);
        if (copyOpt.isEmpty()) {
            throw new IllegalArgumentException("Book copy with barcode " + copyBarcode + " not found.");
        }
        BookCopy copy = copyOpt.get();

        // Find active record
        BorrowRecord activeRecord = recordsById.values().stream()
                .filter(r -> r.getCopyBarcode().equalsIgnoreCase(copyBarcode) && !r.isReturned())
                .findFirst()
                .orElse(null);

        if (activeRecord == null) {
            // Already returned or not currently checked out
            throw new IllegalStateException("Copy " + copyBarcode + " is not currently recorded as checked out.");
        }

        activeRecord.markReturned(LocalDate.now());
        catalogService.updateCopyStatus(copyBarcode, BookStatus.AVAILABLE, null);

        Optional<Student> studentOpt = userService.getStudentById(activeRecord.getStudentId());
        studentOpt.ifPresent(Student::decrementBorrowed);

        // Calculate fine if overdue
        BorrowPolicy policy = getPolicy(Role.STUDENT);
        double fineAmount = fineService.getFineCalculator().calculateFine(activeRecord, policy.getDailyFineRate());
        if (fineAmount > 0) {
            activeRecord.setFineAccrued(fineAmount);
            fineService.createFine(activeRecord.getStudentId(), activeRecord.getRecordId(), activeRecord.getBookTitle(), fineAmount);
            studentOpt.ifPresent(s -> s.addFine(fineAmount));
        }

        todayReturnsCount++;

        String detail = String.format("Returned '%s' [%s] from %s%s",
                activeRecord.getBookTitle(), copyBarcode, activeRecord.getStudentId(),
                fineAmount > 0 ? String.format(" (Fine incurred: ₹%.2f)", fineAmount) : "");

        auditService.logAction(actor != null ? actor : "Librarian #102", "BOOK_RETURNED", detail);

        return activeRecord;
    }

    public synchronized boolean renewBook(int recordId, String actor) {
        BorrowRecord record = recordsById.get(recordId);
        if (record == null || record.isReturned()) return false;

        BorrowPolicy policy = getPolicy(Role.STUDENT);
        if (record.getRenewalCount() >= policy.getMaxRenewals()) {
            throw new IllegalStateException("Max renewals (" + policy.getMaxRenewals() + ") reached.");
        }

        record.incrementRenewal(policy.getLoanPeriodDays());
        auditService.logAction(actor != null ? actor : "Student", "LOAN_RENEWED",
                String.format("Renewed '%s' until %s (Renewal #%d)", record.getBookTitle(), record.getDueDate(), record.getRenewalCount()));
        return true;
    }

    public List<BorrowRecord> getActiveLoansForStudent(String studentId) {
        return recordsById.values().stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(studentId) && !r.isReturned())
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getAllLoansForStudent(String studentId) {
        return recordsById.values().stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(studentId))
                .sorted(Comparator.comparing(BorrowRecord::getIssueDate).reversed())
                .collect(Collectors.toList());
    }

    public long getOverdueCount() {
        LocalDate today = LocalDate.now();
        return recordsById.values().stream()
                .filter(r -> r.isOverdue(today))
                .count();
    }

    public int getTodayIssuesCount() {
        return todayIssuesCount;
    }

    public int getTodayReturnsCount() {
        return todayReturnsCount;
    }

    private void seedInitialLoans() {
        // Seed active loan for Priya Sharma (Book 1)
        Optional<BookCopy> c1 = catalogService.getCopyByBarcode("LIB-000610");
        c1.ifPresent(copy -> {
            copy.setStatus(BookStatus.ISSUED);
            copy.setBorrowerId("2026CS142");
            catalogService.getBookById(copy.getBookId()).ifPresent(Book::recalcAvailable);

            int id = nextRecordId.getAndIncrement();
            BorrowRecord r = new BorrowRecord(id, "2026CS142", copy.getBookId(),
                    "Clean Architecture", copy.getBarcode(), LocalDate.now().minusDays(5), 14);
            recordsById.put(id, r);
            userService.getStudentById("2026CS142").ifPresent(Student::incrementBorrowed);
        });

        // Seed overdue loan for Priya Sharma (Book 2 - Database System Concepts)
        Optional<BookCopy> c2 = catalogService.getCopyByBarcode("LIB-000620");
        c2.ifPresent(copy -> {
            copy.setStatus(BookStatus.ISSUED);
            copy.setBorrowerId("2026CS142");
            catalogService.getBookById(copy.getBookId()).ifPresent(Book::recalcAvailable);

            int id = nextRecordId.getAndIncrement();
            BorrowRecord r = new BorrowRecord(id, "2026CS142", copy.getBookId(),
                    "Database System Concepts", copy.getBarcode(), LocalDate.now().minusDays(34), 14);
            r.setFineAccrued(40.0);
            recordsById.put(id, r);
            userService.getStudentById("2026CS142").ifPresent(Student::incrementBorrowed);
        });
    }
}
