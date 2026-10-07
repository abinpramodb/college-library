/**
 * ==============================================================================
 * 📚 COLLEGE LIBRARY MANAGEMENT SYSTEM (TERMINAL CLI EDITION)
 * Single-File Standalone Java Application — Pure Terminal Interface
 * ==============================================================================
 * Features:
 *   - 100% Terminal-based (No browser, no web server, no GUI dependencies)
 *   - Object-Oriented Architecture (Inheritance, Polymorphism, Strategy & Facade)
 *   - Student Portal: Active loans, renewals, search catalog, pay fines
 *   - Librarian Console: Issue/return books, inventory, copies, members, audit trail
 * 
 * HOW TO RUN:
 *   Option 1: java LibraryTerminalApp.java
 *   Option 2: javac LibraryTerminalApp.java && java LibraryTerminalApp
 * ==============================================================================
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// ==============================================================================
// 1. MAIN ENTRY POINT & TERMINAL UI
// ==============================================================================

public class LibraryTerminalApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static LibrarySystem system;

    public static void main(String[] args) {
        system = new LibrarySystem();

        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n" + "=".repeat(56));
            System.out.println("   MAIN MENU — SELECT PORTAL");
            System.out.println("=".repeat(56));
            System.out.println("  1. 📱 Student Portal (Self-Service)");
            System.out.println("  2. 🛡️  Librarian Console (Desk & Inventory)");
            System.out.println("  3. 📚 View Complete Book Catalogue");
            System.out.println("  4. 📋 View Recent Audit Trail");
            System.out.println("  5. 📊 View Live System Statistics");
            System.out.println("  0. 🚪 Exit Application");
            System.out.print("\nEnter your choice (0-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleStudentMenu();
                case "2" -> handleLibrarianMenu();
                case "3" -> displayCatalogue();
                case "4" -> displayAuditTrail();
                case "5" -> displayStatistics();
                case "0" -> {
                    System.out.println("\n✓ Thank you for using College Library Management System. Goodbye!\n");
                    running = false;
                }
                default -> System.out.println("⚠️ Invalid choice. Please enter a number between 0 and 5.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n" + "=".repeat(62));
        System.out.println("  🏛️  COLLEGE LIBRARY MANAGEMENT SYSTEM — TERMINAL EDITION");
        System.out.println("  Pure Java OOP · Zero Dependencies · Interactive CLI");
        System.out.println("=".repeat(62));
    }

    // --------------------------------------------------------------------------
    // STUDENT PORTAL
    // --------------------------------------------------------------------------
    private static void handleStudentMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("📱 STUDENT LOGIN");
        System.out.println("-".repeat(50));
        System.out.println("Available demo student IDs: 2026-CS-001, 2026-EC-014, 2026CS142");
        System.out.print("Enter Student ID (press Enter for 2026-CS-001): ");
        String sid = scanner.nextLine().trim();
        if (sid.isEmpty()) sid = "2026-CS-001";

        Optional<Student> studentOpt = system.getUserService().getStudentById(sid);
        if (studentOpt.isEmpty()) {
            System.out.println("⚠️ Student ID '" + sid + "' not found in database.");
            return;
        }

        Student s = studentOpt.get();
        boolean inPortal = true;
        while (inPortal) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("📱 STUDENT PORTAL: " + s.getName() + " (" + s.getId() + ")");
            System.out.println("=".repeat(50));
            System.out.println("  Department:        " + s.getDepartment());
            System.out.println("  Borrow Quota:      " + s.getCurrentBorrowedCount() + " / " + s.getMaxBorrowQuota() + " books");
            System.out.println("  Outstanding Fine:  ₹" + String.format("%.2f", s.getOutstandingFine()));
            System.out.println("-".repeat(50));
            System.out.println("  1. 📖 View My Active Loans");
            System.out.println("  2. 🔍 Search Book Catalogue");
            System.out.println("  3. 🔄 Renew a Borrowed Book");
            System.out.println("  4. 💳 Pay Outstanding Fines (Simulated UPI/Cash)");
            System.out.println("  5. 🪪 View Digital Library ID Card");
            System.out.println("  0. ⬅️  Back to Main Menu");
            System.out.print("\nChoice (0-5): ");

            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1" -> {
                    List<BorrowRecord> loans = system.getCirculationService().getActiveLoansForStudent(s.getId());
                    System.out.println("\n--- 📖 Active Loans for " + s.getName() + " ---");
                    if (loans.isEmpty()) {
                        System.out.println("✓ You currently have no books checked out.");
                    } else {
                        for (int i = 0; i < loans.size(); i++) {
                            BorrowRecord r = loans.get(i);
                            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), r.getDueDate());
                            String statusStr = daysLeft < 0 ? ("⚠️ OVERDUE by " + Math.abs(daysLeft) + " day(s)") : (daysLeft + " day(s) remaining");
                            System.out.printf("  %d. [%s] %s%n     Barcode: %s | Due: %s (%s)%n",
                                    (i + 1), r.getRecordId(), r.getBookTitle(), r.getCopyBarcode(), r.getDueDate(), statusStr);
                        }
                    }
                }
                case "2" -> {
                    System.out.print("\nEnter search keyword (title / author / category / ISBN): ");
                    String q = scanner.nextLine().trim();
                    List<Book> matches = system.getCatalogService().searchBooks(q);
                    System.out.println("\n--- Search Results (" + matches.size() + " found) ---");
                    if (matches.isEmpty()) {
                        System.out.println("No matching books found.");
                    } else {
                        for (Book b : matches) {
                            String stockBadge = b.getAvailableCopies() > 0 ? "✓ IN STOCK" : "⚠️ ALL ISSUED";
                            System.out.printf("  • [%d] %s by %s%n    Stock: %d/%d available [%s] | Shelf: %s | ISBN: %s%n",
                                    b.getId(), b.getTitle(), b.getAuthor(), b.getAvailableCopies(), b.getTotalCopies(), stockBadge, b.getShelfLocation(), b.getIsbn());
                        }
                    }
                }
                case "3" -> {
                    List<BorrowRecord> loans = system.getCirculationService().getActiveLoansForStudent(s.getId());
                    if (loans.isEmpty()) {
                        System.out.println("You have no active loans to renew.");
                        break;
                    }
                    System.out.println("\nSelect a loan to renew:");
                    for (int i = 0; i < loans.size(); i++) {
                        System.out.printf("  %d. %s (Barcode: %s | Due: %s)%n",
                                (i + 1), loans.get(i).getBookTitle(), loans.get(i).getCopyBarcode(), loans.get(i).getDueDate());
                    }
                    System.out.print("Enter book number to renew (1-" + loans.size() + ") or 0 to cancel: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim());
                        if (idx > 0 && idx <= loans.size()) {
                            BorrowRecord target = loans.get(idx - 1);
                            BorrowRecord renewed = system.renewBook(target.getCopyBarcode(), s.getName());
                            System.out.println("✓ SUCCESS! Loan renewed. New Due Date: " + renewed.getDueDate());
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }
                case "4" -> {
                    if (s.getOutstandingFine() <= 0) {
                        System.out.println("✓ No outstanding dues! Your account is completely clear.");
                    } else {
                        System.out.println("\nCurrent Dues: ₹" + String.format("%.2f", s.getOutstandingFine()));
                        System.out.println("Select Payment Mode: 1. UPI / QR  2. Cash Desk  0. Cancel");
                        System.out.print("Choice: ");
                        String pChoice = scanner.nextLine().trim();
                        if ("1".equals(pChoice) || "2".equals(pChoice)) {
                            PaymentMethod pm = "1".equals(pChoice) ? PaymentMethod.UPI : PaymentMethod.CASH;
                            system.payAllFines(s.getId(), pm, "TXN-" + System.currentTimeMillis() % 1000000, s.getName());
                            System.out.println("✓ Payment of ₹" + String.format("%.2f", s.getOutstandingFine()) + " cleared via " + pm + "!");
                        }
                    }
                }
                case "5" -> {
                    System.out.println("\n" + "┌".concat("─".repeat(46)).concat("┐"));
                    System.out.println("│          OFFICIAL DIGITAL LIBRARY CARD           │");
                    System.out.println("├".concat("─".repeat(46)).concat("┤"));
                    System.out.printf("│  Name:       %-35s│%n", s.getName());
                    System.out.printf("│  ID / Roll:  %-35s│%n", s.getId());
                    System.out.printf("│  Dept:       %-35s│%n", s.getDepartment());
                    System.out.printf("│  Status:     %-35s│%n", s.getStatus());
                    System.out.println("│  Barcode:    |||| ||| ||||| || |||| |||||        │");
                    System.out.printf("│              * %-31s *│%n", s.getId());
                    System.out.println("└".concat("─".repeat(46)).concat("┘"));
                }
                case "0" -> inPortal = false;
                default -> System.out.println("⚠️ Invalid option.");
            }
        }
    }

    // --------------------------------------------------------------------------
    // LIBRARIAN CONSOLE
    // --------------------------------------------------------------------------
    private static void handleLibrarianMenu() {
        boolean inLibrarian = true;
        while (inLibrarian) {
            System.out.println("\n" + "=".repeat(56));
            System.out.println("🛡️  LIBRARIAN CONSOLE — CIRCULATION & INVENTORY DESK");
            System.out.println("=".repeat(56));
            System.out.println("  1. 📤 Issue Book to Student (Checkout)");
            System.out.println("  2. 📥 Return Book by Barcode (Checkin)");
            System.out.println("  3. ➕ Add New Book Title to Inventory");
            System.out.println("  4. 📋 View & Add Physical Copies of a Book");
            System.out.println("  5. 👥 Search & View Members Directory");
            System.out.println("  6. 🗑️  Delete a Book Title");
            System.out.println("  7. ⚙️  View Current Library Borrowing Policies");
            System.out.println("  0. ⬅️  Back to Main Menu");
            System.out.print("\nChoice (0-7): ");

            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1" -> {
                    System.out.println("\n--- 📤 Issue Book Checkout ---");
                    System.out.print("Enter Student ID (e.g. 2026-CS-001): ");
                    String sid = scanner.nextLine().trim();
                    System.out.print("Enter Book Copy Barcode (e.g. LIB-001-001): ");
                    String barcode = scanner.nextLine().trim();

                    try {
                        BorrowRecord record = system.issueBook(sid, barcode, "Librarian");
                        System.out.println("\n✓ SUCCESS! Book checked out successfully.");
                        System.out.println("  Title:      " + record.getBookTitle());
                        System.out.println("  Student:    " + record.getStudentId());
                        System.out.println("  Due Date:   " + record.getDueDate() + " (14-day standard loan)");
                    } catch (Exception e) {
                        System.out.println("\n❌ FAILED TO ISSUE BOOK: " + e.getMessage());
                    }
                }
                case "2" -> {
                    System.out.println("\n--- 📥 Return Book Checkin ---");
                    System.out.print("Scan / Enter Copy Barcode (e.g. LIB-001-001): ");
                    String barcode = scanner.nextLine().trim();

                    try {
                        BorrowRecord r = system.returnBook(barcode, "Librarian");
                        System.out.println("\n✓ SUCCESS! Book returned to shelf.");
                        System.out.println("  Title:      " + r.getBookTitle());
                        System.out.println("  Student:    " + r.getStudentId());
                        if (r.getFineAccrued() > 0) {
                            System.out.println("  ⚠️ OVERDUE FINE ACCRUED: ₹" + String.format("%.2f", r.getFineAccrued()));
                        } else {
                            System.out.println("  Fine:       ₹0.00 (Returned on time)");
                        }
                    } catch (Exception e) {
                        System.out.println("\n❌ FAILED TO PROCESS RETURN: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.println("\n--- ➕ Add New Book Title ---");
                    System.out.print("Book Title: ");
                    String title = scanner.nextLine().trim();
                    System.out.print("Author: ");
                    String author = scanner.nextLine().trim();
                    System.out.print("ISBN: ");
                    String isbn = scanner.nextLine().trim();
                    System.out.print("Category (e.g. Computer Science): ");
                    String cat = scanner.nextLine().trim();
                    System.out.print("Shelf Code (e.g. CS-05-A): ");
                    String shelf = scanner.nextLine().trim();
                    System.out.print("Initial Physical Copies count (e.g. 3): ");
                    int copies = 1;
                    try {
                        copies = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException ignored) {}

                    Book b = system.addBook(title, author, isbn, cat, "1st", "Academic Press", shelf, "bg-blue-900", copies, "Librarian");
                    System.out.println("\n✓ SUCCESS! Book registered with ID #" + b.getId() + ".");
                    System.out.println("  Generated " + copies + " accession copies with barcodes.");
                }
                case "4" -> {
                    System.out.print("\nEnter Book ID or Title to inspect copies: ");
                    String query = scanner.nextLine().trim();
                    List<Book> matches = system.getCatalogService().searchBooks(query);
                    if (matches.isEmpty()) {
                        System.out.println("No books matched query.");
                        break;
                    }
                    Book b = matches.get(0);
                    System.out.println("\n--- Physical Copies for: " + b.getTitle() + " (ID #" + b.getId() + ") ---");
                    List<BookCopy> copyList = b.getCopies();
                    for (BookCopy c : copyList) {
                        System.out.printf("  • Barcode: %-14s | Status: %-10s | Shelf: %s%n",
                                c.getBarcode(), c.getStatus(), c.getShelfLocation());
                    }
                    System.out.print("\nWould you like to add a new physical copy to this book? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        BookCopy newCopy = system.addCopy(b.getId(), "Librarian");
                        System.out.println("✓ Added new physical copy with barcode: " + newCopy.getBarcode());
                    }
                }
                case "5" -> {
                    System.out.print("\nEnter member name or ID to search (or Enter for all): ");
                    String mq = scanner.nextLine().trim();
                    List<User> users = mq.isEmpty() ? system.getUserService().getAllUsers() : system.getUserService().searchUsers(mq);
                    System.out.println("\n--- Member Directory (" + users.size() + " found) ---");
                    for (User u : users) {
                        System.out.printf("  • [%s] %-20s | Role: %-10s | %s%n",
                                u.getId(), u.getName(), u.getRole(), u.getEmail());
                    }
                }
                case "6" -> {
                    System.out.print("\nEnter Book ID to delete: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        system.deleteBook(bid, "Librarian");
                        System.out.println("✓ Book #" + bid + " removed from catalogue.");
                    } catch (Exception e) {
                        System.out.println("⚠️ " + e.getMessage());
                    }
                }
                case "7" -> {
                    System.out.println("\n--- ⚙️ Library Borrowing Policies ---");
                    System.out.println("  • " + system.getCirculationService().getPolicy(Role.STUDENT));
                    System.out.println("  • " + system.getCirculationService().getPolicy(Role.FACULTY));
                }
                case "0" -> inLibrarian = false;
                default -> System.out.println("⚠️ Invalid option.");
            }
        }
    }

    private static void displayCatalogue() {
        System.out.println("\n" + "=".repeat(75));
        System.out.println("📚 COMPLETE LIBRARY BOOK CATALOGUE");
        System.out.println("=".repeat(75));
        List<Book> books = system.getCatalogService().getAllBooks();
        for (Book b : books) {
            System.out.printf("[ID: %2d] %-34s | %-18s | %d/%d Avail | %s%n",
                    b.getId(),
                    b.getTitle().length() > 34 ? b.getTitle().substring(0, 31) + "..." : b.getTitle(),
                    b.getAuthor().length() > 18 ? b.getAuthor().substring(0, 15) + "..." : b.getAuthor(),
                    b.getAvailableCopies(), b.getTotalCopies(),
                    b.getShelfLocation());
        }
        System.out.println("-".repeat(75));
        System.out.println("Total Unique Titles: " + books.size());
    }

    private static void displayAuditTrail() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📋 RECENT AUDIT TRAIL (IMMUTABLE LOG)");
        System.out.println("=".repeat(70));
        List<AuditLog> logs = system.getAuditService().getRecentLogs(10);
        for (AuditLog l : logs) {
            System.out.printf("[%s] %-14s by %-12s: %s%n",
                    l.getFormattedTimestamp(), l.getAction(), l.getActor(), l.getDetail());
        }
    }

    private static void displayStatistics() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 LIVE SYSTEM DASHBOARD METRICS");
        System.out.println("=".repeat(50));
        Map<String, Object> stats = system.getDashboardStats();
        stats.forEach((k, v) -> System.out.printf("  • %-26s : %s%n", k, v));
    }
}

// ==============================================================================
// 2. ENUMS & DATA TYPES
// ==============================================================================

enum Role { STUDENT, FACULTY, LIBRARIAN }
enum BookStatus { AVAILABLE, ISSUED, RESERVED, DAMAGED, LOST }
enum PaymentMethod { CASH, UPI, ONLINE, WAIVED }
enum PaymentStatus { PENDING, PAID, WAIVED }
enum TransactionType { ISSUE, RETURN, RENEW, FINE_PAID, FINE_WAIVED }

// ==============================================================================
// 3. INTERFACES (OOP ABSTRACTION)
// ==============================================================================

interface Searchable {
    boolean matches(String query);
}

interface Auditable {
    String toAuditString();
}

interface FineCalculator {
    double calculateFine(long daysOverdue, double dailyRate);
}

interface PaymentProcessor {
    boolean processPayment(double amount, String transactionRef);
}

// ==============================================================================
// 4. STRATEGY PATTERN IMPLEMENTATIONS
// ==============================================================================

class StandardFineCalculator implements FineCalculator {
    @Override
    public double calculateFine(long daysOverdue, double dailyRate) {
        if (daysOverdue <= 0) return 0.0;
        return daysOverdue * dailyRate;
    }
}

class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount, String transactionRef) {
        return amount > 0 && transactionRef != null;
    }
}

class CashPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(double amount, String transactionRef) {
        return amount > 0;
    }
}

// ==============================================================================
// 5. DOMAIN MODELS (INHERITANCE & ENCAPSULATION)
// ==============================================================================

abstract class User implements Searchable {
    private final String id;
    private final String name;
    private final String email;
    private final Role role;
    private String status;

    public User(String id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = "ACTIVE";
    }

    public abstract int getMaxBorrowQuota();
    public abstract int getDefaultLoanPeriodDays();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase();
        return id.toLowerCase().contains(q) || name.toLowerCase().contains(q) || email.toLowerCase().contains(q);
    }
}

class Student extends User {
    private final String department;
    private int currentBorrowedCount;
    private double outstandingFine;

    public Student(String id, String name, String email, String department) {
        super(id, name, email, Role.STUDENT);
        this.department = department;
        this.currentBorrowedCount = 0;
        this.outstandingFine = 0.0;
    }

    @Override
    public int getMaxBorrowQuota() { return 3; }
    @Override
    public int getDefaultLoanPeriodDays() { return 14; }

    public String getDepartment() { return department; }
    public int getCurrentBorrowedCount() { return currentBorrowedCount; }
    public void incrementBorrowed() { this.currentBorrowedCount++; }
    public void decrementBorrowed() { if (this.currentBorrowedCount > 0) this.currentBorrowedCount--; }
    public double getOutstandingFine() { return outstandingFine; }
    public void addFine(double amount) { this.outstandingFine += amount; }
    public void clearFines() { this.outstandingFine = 0.0; }
}

class Faculty extends User {
    private final String department;
    private int currentBorrowedCount;

    public Faculty(String id, String name, String email, String department) {
        super(id, name, email, Role.FACULTY);
        this.department = department;
        this.currentBorrowedCount = 0;
    }

    @Override
    public int getMaxBorrowQuota() { return 5; }
    @Override
    public int getDefaultLoanPeriodDays() { return 30; }

    public String getDepartment() { return department; }
    public int getCurrentBorrowedCount() { return currentBorrowedCount; }
}

class Librarian extends User {
    private final String stationId;

    public Librarian(String id, String name, String email, String stationId) {
        super(id, name, email, Role.LIBRARIAN);
        this.stationId = stationId;
    }

    @Override
    public int getMaxBorrowQuota() { return 50; }
    @Override
    public int getDefaultLoanPeriodDays() { return 90; }

    public String getStationId() { return stationId; }
}

class BookCopy {
    private final String barcode;
    private final int bookId;
    private BookStatus status;
    private String shelfLocation;

    public BookCopy(String barcode, int bookId, String shelfLocation) {
        this.barcode = barcode;
        this.bookId = bookId;
        this.shelfLocation = shelfLocation;
        this.status = BookStatus.AVAILABLE;
    }

    public String getBarcode() { return barcode; }
    public int getBookId() { return bookId; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    public String getShelfLocation() { return shelfLocation; }
}

class Book implements Searchable {
    private final int id;
    private final String title;
    private final String author;
    private final String isbn;
    private final String category;
    private final String edition;
    private final String publisher;
    private final String shelfLocation;
    private final String coverColor;
    private final List<BookCopy> copies;

    public Book(int id, String title, String author, String isbn, String category,
                String edition, String publisher, String shelfLocation, String coverColor) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.edition = edition;
        this.publisher = publisher;
        this.shelfLocation = shelfLocation;
        this.coverColor = coverColor;
        this.copies = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public String getEdition() { return edition; }
    public String getPublisher() { return publisher; }
    public String getShelfLocation() { return shelfLocation; }
    public String getCoverColor() { return coverColor; }
    public List<BookCopy> getCopies() { return Collections.unmodifiableList(copies); }

    public void addCopy(BookCopy copy) { this.copies.add(copy); }

    public int getTotalCopies() { return copies.size(); }
    public int getAvailableCopies() {
        return (int) copies.stream().filter(c -> c.getStatus() == BookStatus.AVAILABLE).count();
    }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase();
        return title.toLowerCase().contains(q) ||
               author.toLowerCase().contains(q) ||
               isbn.toLowerCase().contains(q) ||
               category.toLowerCase().contains(q) ||
               shelfLocation.toLowerCase().contains(q);
    }
}

class BorrowRecord implements Auditable {
    private final String recordId;
    private final String studentId;
    private final String copyBarcode;
    private final String bookTitle;
    private final LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAccrued;
    private boolean isReturned;

    public BorrowRecord(String recordId, String studentId, String copyBarcode, String bookTitle, LocalDate issueDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.copyBarcode = copyBarcode;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isReturned = false;
        this.fineAccrued = 0.0;
    }

    public String getRecordId() { return recordId; }
    public String getStudentId() { return studentId; }
    public String getCopyBarcode() { return copyBarcode; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFineAccrued() { return fineAccrued; }
    public void setFineAccrued(double fineAccrued) { this.fineAccrued = fineAccrued; }
    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    @Override
    public String toAuditString() {
        return String.format("Record[%s]: Student=%s, Barcode=%s, Due=%s, Returned=%s",
                recordId, studentId, copyBarcode, dueDate, isReturned);
    }
}

class BorrowPolicy {
    private final Role role;
    private final int maxBooks;
    private final int loanPeriodDays;
    private final double dailyFineRate;

    public BorrowPolicy(Role role, int maxBooks, int loanPeriodDays, double dailyFineRate) {
        this.role = role;
        this.maxBooks = maxBooks;
        this.loanPeriodDays = loanPeriodDays;
        this.dailyFineRate = dailyFineRate;
    }

    public Role getRole() { return role; }
    public int getMaxBooks() { return maxBooks; }
    public int getLoanPeriodDays() { return loanPeriodDays; }
    public double getDailyFineRate() { return dailyFineRate; }

    @Override
    public String toString() {
        return String.format("%s Policy: Max %d books | %d-day loan | ₹%.2f/day overdue fine",
                role, maxBooks, loanPeriodDays, dailyFineRate);
    }
}

class AuditLog {
    private final String logId;
    private final LocalDateTime timestamp;
    private final String action;
    private final String actor;
    private final String detail;

    public AuditLog(String logId, String action, String actor, String detail) {
        this.logId = logId;
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.actor = actor;
        this.detail = detail;
    }

    public String getAction() { return action; }
    public String getActor() { return actor; }
    public String getDetail() { return detail; }
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

// ==============================================================================
// 6. BUSINESS LOGIC SERVICES
// ==============================================================================

class CatalogService {
    private final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private final Map<String, BookCopy> copies = new ConcurrentHashMap<>();
    private final AtomicInteger nextBookId = new AtomicInteger(1);

    public Book registerBook(String title, String author, String isbn, String category,
                             String edition, String publisher, String shelf, String color, int initialCopies) {
        int id = nextBookId.getAndIncrement();
        Book book = new Book(id, title, author, isbn, category, edition, publisher, shelf, color);
        for (int i = 1; i <= initialCopies; i++) {
            String barcode = String.format("LIB-%03d-%03d", id, i);
            BookCopy copy = new BookCopy(barcode, id, shelf);
            book.addCopy(copy);
            copies.put(barcode, copy);
        }
        books.put(id, book);
        return book;
    }

    public BookCopy addCopy(int bookId) {
        Book book = books.get(bookId);
        if (book == null) throw new IllegalArgumentException("Book ID " + bookId + " not found.");
        int copyIndex = book.getTotalCopies() + 1;
        String barcode = String.format("LIB-%03d-%03d", bookId, copyIndex);
        BookCopy copy = new BookCopy(barcode, bookId, book.getShelfLocation());
        book.addCopy(copy);
        copies.put(barcode, copy);
        return copy;
    }

    public void deleteBook(int bookId) {
        Book b = books.get(bookId);
        if (b == null) throw new IllegalArgumentException("Book not found.");
        for (BookCopy c : b.getCopies()) {
            if (c.getStatus() == BookStatus.ISSUED) {
                throw new IllegalStateException("Cannot delete book while copies are currently issued to students.");
            }
            copies.remove(c.getBarcode());
        }
        books.remove(bookId);
    }

    public List<Book> getAllBooks() { return new ArrayList<>(books.values()); }
    public Optional<Book> getBookById(int id) { return Optional.ofNullable(books.get(id)); }
    public Optional<BookCopy> getCopyByBarcode(String barcode) { return Optional.ofNullable(copies.get(barcode)); }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) return getAllBooks();
        return books.values().stream().filter(b -> b.matches(query)).collect(Collectors.toList());
    }

    public int getTotalBooks() { return books.size(); }
    public int getTotalCopies() { return copies.size(); }
}

class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void registerUser(User user) { users.put(user.getId(), user); }
    public Optional<User> getUserById(String id) { return Optional.ofNullable(users.get(id)); }
    public Optional<Student> getStudentById(String id) {
        User u = users.get(id);
        return (u instanceof Student) ? Optional.of((Student) u) : Optional.empty();
    }

    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }
    public List<User> searchUsers(String query) {
        return users.values().stream().filter(u -> u.matches(query)).collect(Collectors.toList());
    }

    public int getStudentCount() {
        return (int) users.values().stream().filter(u -> u.getRole() == Role.STUDENT).count();
    }
    public int getLibrarianCount() {
        return (int) users.values().stream().filter(u -> u.getRole() == Role.LIBRARIAN).count();
    }
}

class CirculationService {
    private final Map<String, BorrowRecord> records = new ConcurrentHashMap<>();
    private final Map<Role, BorrowPolicy> policies = new HashMap<>();
    private final FineCalculator fineCalculator = new StandardFineCalculator();
    private final AtomicInteger nextRecordId = new AtomicInteger(1001);

    public CirculationService() {
        policies.put(Role.STUDENT, new BorrowPolicy(Role.STUDENT, 3, 14, 2.0));
        policies.put(Role.FACULTY, new BorrowPolicy(Role.FACULTY, 5, 30, 1.0));
    }

    public BorrowRecord issueBook(Student student, BookCopy copy, Book book) {
        BorrowPolicy policy = policies.get(Role.STUDENT);
        if (student.getCurrentBorrowedCount() >= policy.getMaxBooks()) {
            throw new IllegalStateException("Student has already borrowed maximum allowed " + policy.getMaxBooks() + " books.");
        }
        if (student.getOutstandingFine() > 0) {
            throw new IllegalStateException("Student has outstanding fines of ₹" + student.getOutstandingFine() + ". Settle dues first.");
        }
        if (copy.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Selected copy is not available (Status: " + copy.getStatus() + ").");
        }

        String recId = "REC-" + nextRecordId.getAndIncrement();
        LocalDate now = LocalDate.now();
        LocalDate due = now.plusDays(policy.getLoanPeriodDays());

        BorrowRecord rec = new BorrowRecord(recId, student.getId(), copy.getBarcode(), book.getTitle(), now, due);
        copy.setStatus(BookStatus.ISSUED);
        student.incrementBorrowed();
        records.put(rec.getRecordId(), rec);
        return rec;
    }

    public BorrowRecord returnBook(String copyBarcode, BookCopy copy, Student student) {
        BorrowRecord rec = records.values().stream()
                .filter(r -> r.getCopyBarcode().equalsIgnoreCase(copyBarcode) && !r.isReturned())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active loan record found for barcode: " + copyBarcode));

        LocalDate now = LocalDate.now();
        rec.setReturnDate(now);
        rec.setReturned(true);
        copy.setStatus(BookStatus.AVAILABLE);
        student.decrementBorrowed();

        long daysOverdue = ChronoUnit.DAYS.between(rec.getDueDate(), now);
        if (daysOverdue > 0) {
            double fine = fineCalculator.calculateFine(daysOverdue, policies.get(Role.STUDENT).getDailyFineRate());
            rec.setFineAccrued(fine);
            student.addFine(fine);
        }
        return rec;
    }

    public BorrowRecord renewBook(String copyBarcode) {
        BorrowRecord rec = records.values().stream()
                .filter(r -> r.getCopyBarcode().equalsIgnoreCase(copyBarcode) && !r.isReturned())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active loan found for barcode: " + copyBarcode));

        LocalDate newDue = rec.getDueDate().plusDays(14);
        rec.setDueDate(newDue);
        return rec;
    }

    public List<BorrowRecord> getActiveLoansForStudent(String studentId) {
        return records.values().stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(studentId) && !r.isReturned())
                .collect(Collectors.toList());
    }

    public BorrowPolicy getPolicy(Role role) { return policies.get(role); }
}

class AuditService {
    private final List<AuditLog> logs = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger logSeq = new AtomicInteger(1);

    public void log(String action, String actor, String detail) {
        logs.add(new AuditLog("LOG-" + logSeq.getAndIncrement(), action, actor, detail));
    }

    public List<AuditLog> getRecentLogs(int limit) {
        int from = Math.max(0, logs.size() - limit);
        List<AuditLog> sub = new ArrayList<>(logs.subList(from, logs.size()));
        Collections.reverse(sub);
        return sub;
    }
}

// ==============================================================================
// 7. CENTRAL DOMAIN FACADE
// ==============================================================================

class LibrarySystem {
    private final CatalogService catalogService = new CatalogService();
    private final UserService userService = new UserService();
    private final CirculationService circulationService = new CirculationService();
    private final AuditService auditService = new AuditService();

    public LibrarySystem() {
        seedSampleData();
    }

    private void seedSampleData() {
        // Users
        userService.registerUser(new Librarian("LIB-001", "Chief Librarian", "librarian@college.edu", "Station #101"));
        userService.registerUser(new Student("2026-CS-001", "Rahul Sharma", "rahul.cs@college.edu", "Computer Science"));
        userService.registerUser(new Student("2026-EC-014", "Ananya Verma", "ananya.ec@college.edu", "Electronics"));
        userService.registerUser(new Student("2026CS142", "Kiran Joseph", "kiran.cs@college.edu", "Computer Science"));
        userService.registerUser(new Faculty("FAC-102", "Dr. A. P. Menon", "menon.prof@college.edu", "Computer Science"));

        // Books & Copies
        catalogService.registerBook("Clean Code: Agile Craftsmanship", "Robert C. Martin", "978-0132350884", "Computer Science", "1st", "Prentice Hall", "CS-01-A", "bg-blue-900", 3);
        catalogService.registerBook("Introduction to Algorithms (CLRS)", "Thomas H. Cormen", "978-0262033848", "Computer Science", "3rd", "MIT Press", "CS-02-B", "bg-indigo-900", 4);
        catalogService.registerBook("Design Patterns: Gang of Four", "Erich Gamma et al.", "978-0201633610", "Computer Science", "1st", "Addison-Wesley", "CS-03-A", "bg-emerald-900", 2);
        catalogService.registerBook("Operating System Concepts", "Abraham Silberschatz", "978-1118063330", "Computer Science", "9th", "Wiley", "CS-04-C", "bg-teal-900", 3);
        catalogService.registerBook("Signals & Linear Systems", "Alan V. Oppenheim", "978-0138147570", "Electronics", "2nd", "Pearson", "EC-01-A", "bg-amber-900", 2);
        catalogService.registerBook("Fundamentals of Thermodynamics", "Richard E. Sonntag", "978-0470041925", "Mechanical", "7th", "Wiley", "ME-02-B", "bg-rose-900", 2);

        // Pre-issued sample loan
        Student s = (Student) userService.getUserById("2026-CS-001").orElseThrow();
        Book b = catalogService.getBookById(1).orElseThrow();
        BookCopy c = b.getCopies().get(0);
        circulationService.issueBook(s, c, b);

        auditService.log("SYSTEM_INIT", "System", "Library core initialized with sample catalogue and students.");
    }

    public BorrowRecord issueBook(String studentId, String copyBarcode, String actor) {
        Student s = userService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student ID '" + studentId + "' not found."));
        BookCopy c = catalogService.getCopyByBarcode(copyBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Copy barcode '" + copyBarcode + "' not found."));
        Book b = catalogService.getBookById(c.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Associated book record not found."));

        BorrowRecord record = circulationService.issueBook(s, c, b);
        auditService.log("BOOK_ISSUE", actor, "Issued '" + b.getTitle() + "' to student " + studentId);
        return record;
    }

    public BorrowRecord returnBook(String copyBarcode, String actor) {
        BookCopy c = catalogService.getCopyByBarcode(copyBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Barcode '" + copyBarcode + "' not found."));
        Book b = catalogService.getBookById(c.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book record not found."));

        List<BorrowRecord> active = circulationService.getActiveLoansForStudent("2026-CS-001");
        // find matching record
        Student student = null;
        for (User u : userService.getAllUsers()) {
            if (u instanceof Student st) {
                for (BorrowRecord r : circulationService.getActiveLoansForStudent(st.getId())) {
                    if (r.getCopyBarcode().equalsIgnoreCase(copyBarcode)) {
                        student = st;
                        break;
                    }
                }
            }
            if (student != null) break;
        }

        if (student == null) {
            throw new IllegalArgumentException("No active checkout found for barcode: " + copyBarcode);
        }

        BorrowRecord rec = circulationService.returnBook(copyBarcode, c, student);
        auditService.log("BOOK_RETURN", actor, "Returned '" + b.getTitle() + "' from student " + student.getId());
        return rec;
    }

    public BorrowRecord renewBook(String copyBarcode, String actor) {
        BorrowRecord rec = circulationService.renewBook(copyBarcode);
        auditService.log("BOOK_RENEW", actor, "Renewed loan on copy " + copyBarcode + " until " + rec.getDueDate());
        return rec;
    }

    public Book addBook(String title, String author, String isbn, String cat, String ed, String pub, String shelf, String col, int copies, String actor) {
        Book b = catalogService.registerBook(title, author, isbn, cat, ed, pub, shelf, col, copies);
        auditService.log("BOOK_ADD", actor, "Added new book '" + title + "' with " + copies + " copies.");
        return b;
    }

    public BookCopy addCopy(int bookId, String actor) {
        BookCopy c = catalogService.addCopy(bookId);
        auditService.log("COPY_ADD", actor, "Added copy " + c.getBarcode() + " to book #" + bookId);
        return c;
    }

    public void deleteBook(int bookId, String actor) {
        catalogService.deleteBook(bookId);
        auditService.log("BOOK_DELETE", actor, "Deleted book #" + bookId);
    }

    public void payAllFines(String studentId, PaymentMethod method, String txnRef, String actor) {
        Student s = userService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        double fine = s.getOutstandingFine();
        s.clearFines();
        auditService.log("FINE_PAYMENT", actor, "Cleared fine of ₹" + fine + " for " + studentId + " via " + method);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Unique Titles", catalogService.getTotalBooks());
        map.put("Total Physical Copies", catalogService.getTotalCopies());
        map.put("Registered Students", userService.getStudentCount());
        map.put("Library Staff / Admins", userService.getLibrarianCount());
        return map;
    }

    public CatalogService getCatalogService() { return catalogService; }
    public UserService getUserService() { return userService; }
    public CirculationService getCirculationService() { return circulationService; }
    public AuditService getAuditService() { return auditService; }
}
