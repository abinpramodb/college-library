package com.library;

import com.library.enums.PaymentMethod;
import com.library.enums.Role;
import com.library.models.*;
import com.library.services.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LibrarySystem {
    private final CatalogService catalogService;
    private final UserService userService;
    private final FineService fineService;
    private final AuditService auditService;
    private final CirculationService circulationService;

    public LibrarySystem() {
        this.catalogService = new CatalogService();
        this.userService = new UserService();
        this.fineService = new FineService();
        this.auditService = new AuditService();
        this.circulationService = new CirculationService(catalogService, userService, fineService, auditService);
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public UserService getUserService() {
        return userService;
    }

    public FineService getFineService() {
        return fineService;
    }

    public AuditService getAuditService() {
        return auditService;
    }

    public CirculationService getCirculationService() {
        return circulationService;
    }

    // High level facade operations
    public Book addBook(String title, String author, String isbn, String category,
                        String edition, String publisher, String shelf, String cover, int copies, String actor) {
        Book book = catalogService.addBook(title, author, isbn, category, edition, publisher, shelf, cover, copies);
        auditService.logAction(actor != null ? actor : "Librarian #102", "BOOK_ADDED",
                String.format("Added '%s' — %d copies shelved at %s", title, copies, shelf));
        return book;
    }

    public BorrowRecord issueBook(String studentId, String copyBarcode, String actor) {
        return circulationService.issueBook(studentId, copyBarcode, actor);
    }

    public BorrowRecord returnBook(String copyBarcode, String actor) {
        return circulationService.returnBook(copyBarcode, actor);
    }

    public boolean renewBook(int recordId, String actor) {
        return circulationService.renewBook(recordId, actor);
    }

    public boolean payFine(int fineId, String studentId, PaymentMethod method, String transactionRef, String actor) {
        Optional<Student> studentOpt = userService.getStudentById(studentId);
        boolean success = fineService.payFine(fineId, method, transactionRef, studentOpt.orElse(null));
        if (success) {
            auditService.logAction(actor != null ? actor : "Student (" + studentId + ")", "FINE_PAID",
                    String.format("Fine #%d paid via %s (Ref: %s)", fineId, method.getLabel(), transactionRef));
        }
        return success;
    }

    public boolean payAllFines(String studentId, PaymentMethod method, String transactionRef, String actor) {
        Optional<Student> studentOpt = userService.getStudentById(studentId);
        boolean success = fineService.payAllFinesForStudent(studentId, method, transactionRef, studentOpt.orElse(null));
        if (success) {
            auditService.logAction(actor != null ? actor : "Student (" + studentId + ")", "FINE_PAID",
                    String.format("All outstanding fines cleared for %s via %s (Ref: %s)", studentId, method.getLabel(), transactionRef));
        }
        return success;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayIssues", circulationService.getTodayIssuesCount());
        stats.put("todayReturns", circulationService.getTodayReturnsCount());
        stats.put("overdueBooks", circulationService.getOverdueCount());
        stats.put("fineCollectedToday", fineService.getTotalFinesCollectedToday());
        stats.put("totalBooks", catalogService.getAllBooks().size());
        stats.put("totalMembers", userService.getAllUsers().size());
        return stats;
    }
}
