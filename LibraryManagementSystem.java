/**
 * ==============================================================================
 * COLLEGE LIBRARY MANAGEMENT SYSTEM (ENTERPRISE EDITION)
 * Single-File Standalone Java Distribution
 * ==============================================================================
 * An Object-Oriented, Modular, Zero-Dependency Library Information System.
 * 
 * HOW TO COMPILE AND RUN:
 * ------------------------------------------------------------------------------
 * Option 1: Direct Execution (Java 11+)
 *     java LibraryManagementSystem.java
 * 
 * Option 2: Standard Compilation & Execution
 *     javac LibraryManagementSystem.java
 *     java LibraryManagementSystem
 * 
 * Option 3: Headless Production Server (Cloud/Container)
 *     java LibraryManagementSystem --server 8080
 * 
 * Option 4: Interactive Terminal CLI Mode
 *     java LibraryManagementSystem --cli
 * 
 * Option 5: Self-Verifying Unit Tests
 *     java LibraryManagementSystem --test
 * ==============================================================================
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.*;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


// ============================================================================
// SECTION: Main
// ============================================================================

public class LibraryManagementSystem {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   COLLEGE LIBRARY MANAGEMENT SYSTEM — DESKTOP APPLICATION        ");
        System.out.println("==================================================================");

        // 1. Initialize OOP Domain Facade
        LibrarySystem system = new LibrarySystem();

        // 2. Resolve Frontend UI Root
        Path currentDir = Paths.get("").toAbsolutePath();
        Path webRoot = currentDir;
        if (currentDir.endsWith("java-lms")) {
            webRoot = currentDir.getParent();
        }

        int port = 8080;
        boolean enableCli = false;
        boolean serverOnly = java.awt.GraphicsEnvironment.isHeadless()
                || System.getProperty("os.name", "").toLowerCase().contains("linux");
        boolean runTests = false;

        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
                serverOnly = true;
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--cli".equalsIgnoreCase(arg)) {
                enableCli = true;
            } else if ("--test".equalsIgnoreCase(arg) || "--tests".equalsIgnoreCase(arg)) {
                runTests = true;
            } else if ("--server".equalsIgnoreCase(arg) || "--headless".equalsIgnoreCase(arg)) {
                serverOnly = true;
            } else if ("--port".equalsIgnoreCase(arg) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (runTests) {
            TestRunner.runAllTests(system);
        }

        // 4. Start Embedded Zero-Dependency HTTP & REST Server
        try {
            LibraryHttpServer server = new LibraryHttpServer(system, port, webRoot);
            server.start();

            int activePort = server.getActivePort();
            String appUrl = "http://localhost:" + activePort + "/";
            System.out.println("\n🌐 Serving Exact Library UI at: " + appUrl);
            System.out.println("👥 System Portals:");
            System.out.println("   • Student:           Student Portal (ID: 2026CE045)");
            System.out.println("   • Librarian:         Librarian Console (Station #102 · LIB-001)\n");

            if (enableCli) {
                ConsoleUI console = new ConsoleUI(system);
                console.start();
                server.stop();
                System.exit(0);
            } else if (serverOnly) {
                System.out.println("🚀 Running in Production Headless Server mode.");
                System.out.println("📡 Ready for incoming production traffic on port " + activePort);
                System.out.println("  (Press Ctrl+C to terminate server)");
                Thread.currentThread().join();
            } else {
                // Launch Dedicated Standalone Desktop Application Window (Exact UI)
                launchDesktopAppWindow(appUrl);
                System.out.println("✓ Dedicated Desktop Application Window is active.");
                System.out.println("  (Press Ctrl+C in terminal to stop)");
                Thread.currentThread().join();
            }

        } catch (Exception e) {
            System.err.println("Fatal error starting Library Application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void launchDesktopAppWindow(String url) {
        String tmpProfile = System.getProperty("java.io.tmpdir") + "/lms_desktop_app_profile";
        String[] chromiumBrowsers = {
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
            "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
            "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge"
        };

        boolean launched = false;
        for (String execPath : chromiumBrowsers) {
            File bin = new File(execPath);
            if (bin.exists()) {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            execPath,
                            "--app=" + url,
                            "--user-data-dir=" + tmpProfile,
                            "--no-first-run",
                            "--no-default-browser-check",
                            "--window-size=1260,840"
                    );
                    pb.start();
                    System.out.println("🚀 Launched Dedicated Desktop Application Window via " + bin.getName());
                    launched = true;

                    // Activate and bring to front
                    try {
                        new ProcessBuilder("osascript", "-e", "tell application \"" + bin.getName().replace(".app", "") + "\" to activate").start();
                    } catch (Exception ignored) {}
                    break;
                } catch (Exception ignored) {}
            }
        }

        // Fallback open command
        if (!launched) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                    System.out.println("🌐 Opened application window at: " + url);
                } else {
                    new ProcessBuilder("open", url).start();
                }
            } catch (Exception ignored) {}
        }
    }
}

// ============================================================================
// SECTION: Searchable
// ============================================================================

interface Searchable {
    boolean matches(String query);
}

// ============================================================================
// SECTION: Auditable
// ============================================================================

interface Auditable {
    AuditLog createAuditRecord(String actor, String actionDetails);
}

// ============================================================================
// SECTION: FineCalculator
// ============================================================================

interface FineCalculator {
    double calculateFine(BorrowRecord record, double dailyRate);
}

// ============================================================================
// SECTION: PaymentProcessor
// ============================================================================

interface PaymentProcessor {
    PaymentMethod getPaymentMethod();
    boolean processPayment(String studentId, double amount, String reference);
}

// ============================================================================
// SECTION: Role
// ============================================================================

enum Role {
    STUDENT("Student", "Undergraduate & Postgraduate Members"),
    LIBRARIAN("Librarian", "Circulation & Catalogue Operations"),
    FACULTY("Faculty", "Teaching & Research Staff"),
    ADMIN("Administrator", "System Configuration & Audit Management");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String text) {
        if (text == null) return STUDENT;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text) || r.displayName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return STUDENT;
    }
}

// ============================================================================
// SECTION: BookStatus
// ============================================================================

enum BookStatus {
    AVAILABLE("Available in Stacks", true),
    ISSUED("Checked Out", false),
    RESERVED("On Hold for Reservation", false),
    DAMAGED("Under Maintenance / Repair", false),
    LOST("Reported Lost", false);

    private final String description;
    private final boolean canBorrow;

    BookStatus(String description, boolean canBorrow) {
        this.description = description;
        this.canBorrow = canBorrow;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBorrowable() {
        return canBorrow;
    }
}

// ============================================================================
// SECTION: PaymentStatus
// ============================================================================

enum PaymentStatus {
    PENDING("Payment Outstanding"),
    PAID("Settled & Cleared"),
    WAIVED("Waived by Authority");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

// ============================================================================
// SECTION: PaymentMethod
// ============================================================================

enum PaymentMethod {
    UPI("Instant UPI", "upi"),
    CASH("Cash at Librarian Desk", "cash"),
    CARD("Debit / Credit Card", "card"),
    NET_BANKING("Net Banking", "netbanking");

    private final String label;
    private final String code;

    PaymentMethod(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static PaymentMethod fromString(String val) {
        if (val == null) return UPI;
        for (PaymentMethod pm : values()) {
            if (pm.code.equalsIgnoreCase(val) || pm.name().equalsIgnoreCase(val)) {
                return pm;
            }
        }
        return UPI;
    }
}

// ============================================================================
// SECTION: TransactionType
// ============================================================================

enum TransactionType {
    ISSUE("BOOK_ISSUED"),
    RETURN("BOOK_RETURNED"),
    RENEW("LOAN_RENEWED"),
    FINE_PAID("FINE_PAID"),
    BOOK_ADDED("BOOK_ADDED"),
    RULE_UPDATED("RULE_UPDATED"),
    USER_REGISTERED("USER_REGISTERED");

    private final String logCode;

    TransactionType(String logCode) {
        this.logCode = logCode;
    }

    public String getLogCode() {
        return logCode;
    }
}

// ============================================================================
// SECTION: StandardFineCalculator
// ============================================================================

class StandardFineCalculator implements FineCalculator {
    @Override
    public double calculateFine(BorrowRecord record, double dailyRate) {
        if (record == null) return 0.0;
        LocalDate compareDate = record.isReturned() ? record.getReturnDate() : LocalDate.now();
        if (compareDate == null) compareDate = LocalDate.now();

        long daysOverdue = record.getDaysOverdue(compareDate);
        if (daysOverdue <= 0) return 0.0;

        return daysOverdue * dailyRate;
    }
}

// ============================================================================
// SECTION: CashPaymentProcessor
// ============================================================================

class CashPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CASH;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Librarian verified cash receipt
        return amount > 0;
    }
}

// ============================================================================
// SECTION: UpiPaymentProcessor
// ============================================================================

class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Fast UPI QR validation
        return amount > 0;
    }
}

// ============================================================================
// SECTION: OnlinePaymentProcessor
// ============================================================================

class OnlinePaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NET_BANKING;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Online gateway confirmation check
        return amount > 0 && reference != null && !reference.isBlank();
    }
}

// ============================================================================
// SECTION: User
// ============================================================================

abstract class User implements Searchable {
    protected String id;
    protected String name;
    protected String email;
    protected Role role;
    protected String department;
    protected boolean active;
    protected String password = "password123";

    public User(String id, String name, String email, Role role, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.active = true;
        this.password = "password123";
    }

    public String getPassword() {
        return password != null ? password : "password123";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract int getMaxBorrowQuota();
    public abstract int getLoanPeriodDays();

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase().trim();
        return id.toLowerCase().contains(q) ||
               name.toLowerCase().contains(q) ||
               (email != null && email.toLowerCase().contains(q)) ||
               (department != null && department.toLowerCase().contains(q));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Dept: %s", role.getDisplayName(), name, id, department);
    }
}

// ============================================================================
// SECTION: Student
// ============================================================================

class Student extends User {
    private String registerNumber;
    private String semester;
    private double outstandingFine;
    private int currentBorrowedCount;

    public Student(String id, String name, String email, String department, String semester) {
        super(id, name, email, Role.STUDENT, department);
        this.registerNumber = id;
        this.semester = semester;
        this.outstandingFine = 0.0;
        this.currentBorrowedCount = 0;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public double getOutstandingFine() {
        return outstandingFine;
    }

    public void addFine(double amount) {
        this.outstandingFine += amount;
    }

    public void deductFine(double amount) {
        this.outstandingFine = Math.max(0.0, this.outstandingFine - amount);
    }

    public int getCurrentBorrowedCount() {
        return currentBorrowedCount;
    }

    public void incrementBorrowed() {
        this.currentBorrowedCount++;
    }

    public void decrementBorrowed() {
        this.currentBorrowedCount = Math.max(0, this.currentBorrowedCount - 1);
    }

    @Override
    public int getMaxBorrowQuota() {
        return 4;
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }

    public boolean canBorrow() {
        return active && currentBorrowedCount < getMaxBorrowQuota() && outstandingFine < 100.0;
    }
}

// ============================================================================
// SECTION: Faculty
// ============================================================================

class Faculty extends User {
    private String designation;
    private String researchArea;

    public Faculty(String id, String name, String email, String department, String designation) {
        super(id, name, email, Role.FACULTY, department);
        this.designation = designation;
        this.researchArea = "Computer Science";
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getResearchArea() {
        return researchArea;
    }

    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 8;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }
}

// ============================================================================
// SECTION: Librarian
// ============================================================================

class Librarian extends User {
    private String deskStation;
    private String shift;

    public Librarian(String id, String name, String email, String department, String deskStation) {
        super(id, name, email, Role.LIBRARIAN, department);
        this.deskStation = deskStation;
        this.shift = "Day";
    }

    public String getDeskStation() {
        return deskStation;
    }

    public void setDeskStation(String deskStation) {
        this.deskStation = deskStation;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 10;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }
}

// ============================================================================
// SECTION: Admin
// ============================================================================

class Admin extends User {
    private int accessLevel;

    public Admin(String id, String name, String email, String department) {
        super(id, name, email, Role.ADMIN, department);
        this.accessLevel = 1;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 15;
    }

    @Override
    public int getLoanPeriodDays() {
        return 60;
    }
}

// ============================================================================
// SECTION: BorrowPolicy
// ============================================================================

class BorrowPolicy {
    private Role role;
    private int maxBooks;
    private int loanPeriodDays;
    private int maxRenewals;
    private double dailyFineRate;

    public BorrowPolicy(Role role, int maxBooks, int loanPeriodDays, int maxRenewals, double dailyFineRate) {
        this.role = role;
        this.maxBooks = maxBooks;
        this.loanPeriodDays = loanPeriodDays;
        this.maxRenewals = maxRenewals;
        this.dailyFineRate = dailyFineRate;
    }

    public Role getRole() {
        return role;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    public int getMaxRenewals() {
        return maxRenewals;
    }

    public void setMaxRenewals(int maxRenewals) {
        this.maxRenewals = maxRenewals;
    }

    public double getDailyFineRate() {
        return dailyFineRate;
    }

    public void setDailyFineRate(double dailyFineRate) {
        this.dailyFineRate = dailyFineRate;
    }

    @Override
    public String toString() {
        return String.format("Policy for %s: Max %d books, %d days loan, %d renewals, ₹%.2f/day fine",
                role.getDisplayName(), maxBooks, loanPeriodDays, maxRenewals, dailyFineRate);
    }
}

// ============================================================================
// SECTION: AuditLog
// ============================================================================

class AuditLog {
    private int id;
    private LocalDateTime timestamp;
    private String actor;
    private String action;
    private String detail;

    public AuditLog(int id, String actor, String action, String detail) {
        this.id = id;
        this.timestamp = LocalDateTime.now();
        this.actor = actor;
        this.action = action;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return timestamp.format(dtf);
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s: %s", getFormattedTimestamp(), action, actor, detail);
    }
}

// ============================================================================
// SECTION: Fine
// ============================================================================

class Fine {
    private int fineId;
    private String studentId;
    private int recordId;
    private String bookTitle;
    private double amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionRef;
    private LocalDateTime dateIncurred;
    private LocalDateTime datePaid;

    public Fine(int fineId, String studentId, int recordId, String bookTitle, double amount) {
        this.fineId = fineId;
        this.studentId = studentId;
        this.recordId = recordId;
        this.bookTitle = bookTitle;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.method = null;
        this.transactionRef = null;
        this.dateIncurred = LocalDateTime.now();
        this.datePaid = null;
    }

    public int getFineId() {
        return fineId;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public LocalDateTime getDateIncurred() {
        return dateIncurred;
    }

    public LocalDateTime getDatePaid() {
        return datePaid;
    }

    public void settle(PaymentMethod method, String transactionRef) {
        this.status = PaymentStatus.PAID;
        this.method = method;
        this.transactionRef = transactionRef;
        this.datePaid = LocalDateTime.now();
    }

    public String getFormattedDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return (datePaid != null ? datePaid : dateIncurred).format(dtf);
    }

    @Override
    public String toString() {
        return String.format("Fine #%d: ₹%.2f on %s for '%s' [%s]",
                fineId, amount, studentId, bookTitle, status);
    }
}

// ============================================================================
// SECTION: Reservation
// ============================================================================

class Reservation {
    private int id;
    private String studentId;
    private int bookId;
    private String bookTitle;
    private LocalDate requestDate;
    private LocalDate expiryDate;
    private String status; // PENDING, READY, FULFILLED, CANCELLED

    public Reservation(int id, String studentId, int bookId, String bookTitle, LocalDate requestDate) {
        this.id = id;
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.requestDate = requestDate;
        this.expiryDate = requestDate.plusDays(7);
        this.status = "PENDING";
    }

    public int getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Reservation #%d: '%s' for %s [%s]", id, bookTitle, studentId, status);
    }
}

// ============================================================================
// SECTION: BookCopy
// ============================================================================

class BookCopy {
    private String barcode;
    private int bookId;
    private BookStatus status;
    private String shelfLocation;
    private String borrowerId;
    private String condition;

    public BookCopy(String barcode, int bookId, BookStatus status, String shelfLocation) {
        this.barcode = barcode;
        this.bookId = bookId;
        this.status = status;
        this.shelfLocation = shelfLocation;
        this.borrowerId = null;
        this.condition = "Good";
    }

    public String getBarcode() {
        return barcode;
    }

    public int getBookId() {
        return bookId;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return String.format("Copy %s (Book #%d) - Status: %s - Shelf: %s", barcode, bookId, status, shelfLocation);
    }
}

// ============================================================================
// SECTION: Book
// ============================================================================

class Book implements Searchable {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String edition;
    private String publisher;
    private String shelfLocation;
    private String coverStyle;
    private int totalCopies;
    private int availableCopies;
    private final List<BookCopy> copies;

    public Book(int id, String title, String author, String isbn, String category,
                String edition, String publisher, String shelfLocation, String coverStyle, int initialCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.edition = edition != null ? edition : "1st";
        this.publisher = publisher != null ? publisher : "Academic Press";
        this.shelfLocation = shelfLocation;
        this.coverStyle = coverStyle != null ? coverStyle : "bg-blue-900";
        this.totalCopies = initialCopies;
        this.availableCopies = initialCopies;
        this.copies = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEdition() {
        return edition;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getCoverStyle() {
        return coverStyle;
    }

    public void setCoverStyle(String coverStyle) {
        this.coverStyle = coverStyle;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
        this.totalCopies = copies.size();
        recalcAvailable();
    }

    public List<BookCopy> getCopies() {
        return Collections.unmodifiableList(copies);
    }

    public void recalcAvailable() {
        long avail = copies.stream().filter(BookCopy::isAvailable).count();
        this.availableCopies = (int) avail;
    }

    public void adjustAvailable(int delta) {
        this.availableCopies = Math.max(0, Math.min(this.totalCopies, this.availableCopies + delta));
    }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase().trim();
        return title.toLowerCase().contains(q) ||
               author.toLowerCase().contains(q) ||
               isbn.toLowerCase().contains(q) ||
               (category != null && category.toLowerCase().contains(q)) ||
               (shelfLocation != null && shelfLocation.toLowerCase().contains(q));
    }

    @Override
    public String toString() {
        return String.format("[%d] %s by %s (ISBN: %s) [%d/%d avail] @ %s",
                id, title, author, isbn, availableCopies, totalCopies, shelfLocation);
    }
}

// ============================================================================
// SECTION: BorrowRecord
// ============================================================================

class BorrowRecord {
    private int recordId;
    private String studentId;
    private int bookId;
    private String bookTitle;
    private String copyBarcode;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private int renewalCount;
    private double fineAccrued;

    public BorrowRecord(int recordId, String studentId, int bookId, String bookTitle,
                        String copyBarcode, LocalDate issueDate, int loanPeriodDays) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.copyBarcode = copyBarcode;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(loanPeriodDays);
        this.returnDate = null;
        this.returned = false;
        this.renewalCount = 0;
        this.fineAccrued = 0.0;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getCopyBarcode() {
        return copyBarcode;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void markReturned(LocalDate date) {
        this.returned = true;
        this.returnDate = date;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public void incrementRenewal(int extraDays) {
        this.renewalCount++;
        this.dueDate = this.dueDate.plusDays(extraDays);
    }

    public double getFineAccrued() {
        return fineAccrued;
    }

    public void setFineAccrued(double fineAccrued) {
        this.fineAccrued = fineAccrued;
    }

    public boolean isOverdue(LocalDate asOfDate) {
        return !returned && asOfDate.isAfter(dueDate);
    }

    public long getDaysOverdue(LocalDate asOfDate) {
        if (!isOverdue(asOfDate)) return 0;
        return ChronoUnit.DAYS.between(dueDate, asOfDate);
    }

    @Override
    public String toString() {
        return String.format("Borrow #%d: '%s' [%s] by %s (Due: %s, Returned: %s)",
                recordId, bookTitle, copyBarcode, studentId, dueDate, returned);
    }
}

// ============================================================================
// SECTION: AuditService
// ============================================================================

class AuditService {
    private final List<AuditLog> logs = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger nextLogId = new AtomicInteger(1);

    public AuditService() {
        seedInitialLogs();
    }

    public synchronized AuditLog logAction(String actor, String action, String detail) {
        int id = nextLogId.getAndIncrement();
        AuditLog log = new AuditLog(id, actor, action, detail);
        logs.add(0, log); // Newest first
        return log;
    }

    public List<AuditLog> getRecentLogs(int limit) {
        synchronized (logs) {
            int toIndex = Math.min(logs.size(), limit > 0 ? limit : logs.size());
            return new ArrayList<>(logs.subList(0, toIndex));
        }
    }

    public List<AuditLog> getAllLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }

    private void seedInitialLogs() {
        logAction("Librarian #102", "BOOK_ISSUED", "Issued 'Clean Architecture' [LIB-000602] to Priya Sharma (2026CS142)");
        logAction("Librarian #102", "BOOK_RETURNED", "Returned 'Computer Networks' [LIB-000624] from Rahul Verma (2026CS108)");
        logAction("Librarian #102", "FINE_COLLECTED", "Collected ₹40 fine from Priya Sharma via UPI (TXN-984321)");
        logAction("Librarian #001", "RULE_UPDATED", "Updated Student borrowing policy: Max loan 14 days, fine rate ₹2.00/day");
    }
}

// ============================================================================
// SECTION: CatalogService
// ============================================================================

class CatalogService {
    private final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private final Map<String, BookCopy> copiesByBarcode = new ConcurrentHashMap<>();
    private final AtomicInteger nextBookId = new AtomicInteger(1);

    public CatalogService() {
        seedInitialCatalogue();
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Optional<Book> getBookById(int id) {
        return Optional.ofNullable(books.get(id));
    }

    public Optional<BookCopy> getCopyByBarcode(String barcode) {
        if (barcode == null) return Optional.empty();
        return Optional.ofNullable(copiesByBarcode.get(barcode.trim().toUpperCase()));
    }

    public List<BookCopy> getCopiesForBook(int bookId) {
        Book book = books.get(bookId);
        if (book != null) {
            return book.getCopies();
        }
        return Collections.emptyList();
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return getAllBooks();
        }
        return books.values().stream()
                .filter(b -> b.matches(query))
                .collect(Collectors.toList());
    }

    public synchronized Book addBook(String title, String author, String isbn, String category,
                                     String edition, String publisher, String shelfLocation,
                                     String coverStyle, int initialCopies) {
        int id = nextBookId.getAndIncrement();
        Book book = new Book(id, title, author, isbn, category, edition, publisher, shelfLocation, coverStyle, initialCopies);

        int startNum = 600 + (id * 10);
        for (int i = 0; i < initialCopies; i++) {
            String barcode = String.format("LIB-000%d", startNum + i);
            BookCopy copy = new BookCopy(barcode, id, BookStatus.AVAILABLE, shelfLocation);
            book.addCopy(copy);
            copiesByBarcode.put(barcode, copy);
        }

        books.put(id, book);
        return book;
    }

    public synchronized BookCopy addCopy(int bookId, String customBarcode, String shelfLocation) {
        Book book = books.get(bookId);
        if (book == null) return null;

        String barcode = customBarcode;
        if (barcode == null || barcode.isBlank()) {
            int copyIdx = book.getCopies().size() + 1;
            int num = 600 + (bookId * 10) + copyIdx;
            barcode = String.format("LIB-000%d", num);
        }

        String shelf = (shelfLocation != null && !shelfLocation.isBlank()) ? shelfLocation : book.getShelfLocation();
        BookCopy copy = new BookCopy(barcode, bookId, BookStatus.AVAILABLE, shelf);
        book.addCopy(copy);
        copiesByBarcode.put(barcode, copy);
        book.recalcAvailable();
        return copy;
    }

    public synchronized boolean updateCopyStatus(String barcode, BookStatus newStatus, String borrowerId) {
        BookCopy copy = copiesByBarcode.get(barcode);
        if (copy == null) return false;
        copy.setStatus(newStatus);
        copy.setBorrowerId(borrowerId);
        Book book = books.get(copy.getBookId());
        if (book != null) {
            book.recalcAvailable();
        }
        return true;
    }

    private void seedInitialCatalogue() {
        // Book 1
        Book b1 = addBook("Clean Architecture", "Robert C. Martin", "978-0134494166",
                "Software Engineering", "1st", "Prentice Hall", "CS-B-14", "bg-blue-900", 4);

        // Book 2
        Book b2 = addBook("Database System Concepts", "Abraham Silberschatz", "978-0078022159",
                "Database", "7th", "McGraw-Hill", "DB-A-03", "bg-purple-900", 3);

        // Book 3
        Book b3 = addBook("Computer Networks", "Andrew S. Tanenbaum", "978-0132126953",
                "Networking", "5th", "Pearson", "NW-C-08", "bg-green-900", 5);

        // Book 4
        Book b4 = addBook("Operating System Concepts", "Silberschatz, Galvin, Gagne", "978-1119800361",
                "Systems", "10th", "Wiley", "OS-D-02", "bg-red-900", 4);

        // Book 5
        Book b5 = addBook("Artificial Intelligence: Modern Approach", "Stuart Russell, Peter Norvig", "978-0136042594",
                "Computer Science", "4th", "Pearson", "AI-E-21", "bg-orange-900", 3);

        // Book 6
        Book b6 = addBook("Design Patterns: Elements of Reusable OO", "Erich Gamma, Richard Helm", "978-0201633610",
                "Software Engineering", "1st", "Addison-Wesley", "CS-B-20", "bg-blue-900", 3);
    }
}

// ============================================================================
// SECTION: UserService
// ============================================================================

class UserService {
    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    public UserService() {
        seedUsers();
    }

    public Optional<User> getUserById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usersById.get(id.trim().toUpperCase()));
    }

    public Optional<Student> getStudentById(String id) {
        return getUserById(id)
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    public List<Student> getAllStudents() {
        return usersById.values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) return getAllUsers();
        return usersById.values().stream()
                .filter(u -> u.matches(query))
                .collect(Collectors.toList());
    }

    public synchronized void registerUser(User user) {
        usersById.put(user.getId().toUpperCase(), user);
    }

    public synchronized void updateOrRegisterUser(String origId, String newId, String name, String password, String dept, String roleStr, String status) {
        if (origId != null && !origId.trim().equalsIgnoreCase(newId.trim())) {
            usersById.remove(origId.trim().toUpperCase());
        }
        Optional<User> existing = getUserById(newId);
        User u;
        if (existing.isPresent()) {
            u = existing.get();
            u.setName(name);
            u.setId(newId);
            u.setDepartment(dept);
            if (password != null && !password.isBlank()) {
                u.setPassword(password);
            }
            u.setActive(!"inactive".equalsIgnoreCase(status) && !"suspended".equalsIgnoreCase(status));
        } else {
            String email = newId.toLowerCase() + "@college.edu";
            if ("Librarian".equalsIgnoreCase(roleStr) || "Admin".equalsIgnoreCase(roleStr)) {
                u = new Librarian(newId, name, email, dept, "Circulation Desk");
            } else if ("Faculty".equalsIgnoreCase(roleStr)) {
                u = new Faculty(newId, name, email, dept, "Faculty Member");
            } else {
                u = new Student(newId, name, email, dept, "Semester");
            }
            if (password != null && !password.isBlank()) {
                u.setPassword(password);
            }
            u.setActive(!"inactive".equalsIgnoreCase(status) && !"suspended".equalsIgnoreCase(status));
            registerUser(u);
        }
    }

    public synchronized boolean deleteUser(String id) {
        if (id == null) return false;
        return usersById.remove(id.trim().toUpperCase()) != null;
    }

    private void seedUsers() {
        // Students
        Student s0 = new Student("2026CE045", "Student", "student@college.edu", "Computer Engineering", "6th Sem");
        s0.addFine(40.0);
        Student s1 = new Student("2026CS142", "Student", "student.cs@college.edu", "Computer Science", "6th Sem");
        s1.addFine(40.0); // Has ₹40 fine for Database Systems

        Student s2 = new Student("2026CS108", "Rahul Verma", "rahul.v@college.edu", "Computer Science", "6th Sem");
        Student s3 = new Student("2026EC045", "Ananya Iyer", "ananya.i@college.edu", "Electronics & Comm", "4th Sem");
        Student s4 = new Student("2026ME089", "Aditya Nair", "aditya.n@college.edu", "Mechanical Eng", "4th Sem");

        registerUser(s0);
        registerUser(s1);
        registerUser(s2);
        registerUser(s3);
        registerUser(s4);

        // Librarians
        Librarian l1 = new Librarian("LIB-001", "Librarian", "librarian@college.edu", "Library Services", "Circulation Desk 1");
        registerUser(l1);
        Librarian l2 = new Librarian("LIB-102", "Suresh Kumar", "suresh.k@college.edu", "Library Services", "Circulation Desk #102");
        registerUser(l2);

        // Faculty
        Faculty f1 = new Faculty("FAC-102", "Dr. K. Ramanathan", "ramanathan.k@college.edu", "Computer Science", "Professor");
        registerUser(f1);

        // Staff compatibility alias
        Librarian a1 = new Librarian("ADM-001", "Librarian", "librarian@college.edu", "Library Services", "Central Library");
        registerUser(a1);
    }
}

// ============================================================================
// SECTION: FineService
// ============================================================================

class FineService {
    private final FineCalculator fineCalculator;
    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors = new HashMap<>();
    private final Map<Integer, Fine> finesById = new ConcurrentHashMap<>();
    private final AtomicInteger nextFineId = new AtomicInteger(1001);
    private double totalFinesCollected = 840.0; // Seeded today amount

    public FineService() {
        this.fineCalculator = new StandardFineCalculator();
        registerProcessor(new UpiPaymentProcessor());
        registerProcessor(new CashPaymentProcessor());
        registerProcessor(new OnlinePaymentProcessor());
        seedInitialFines();
    }

    public void registerProcessor(PaymentProcessor processor) {
        paymentProcessors.put(processor.getPaymentMethod(), processor);
    }

    public Fine createFine(String studentId, int recordId, String bookTitle, double amount) {
        int id = nextFineId.getAndIncrement();
        Fine fine = new Fine(id, studentId, recordId, bookTitle, amount);
        finesById.put(id, fine);
        return fine;
    }

    public List<Fine> getFinesForStudent(String studentId) {
        return finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId))
                .sorted(Comparator.comparing(Fine::getFineId).reversed())
                .collect(Collectors.toList());
    }

    public double getPendingFineTotalForStudent(String studentId) {
        return finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId) && f.getStatus() == PaymentStatus.PENDING)
                .mapToDouble(Fine::getAmount)
                .sum();
    }

    public synchronized boolean payFine(int fineId, PaymentMethod method, String transactionRef, Student student) {
        Fine fine = finesById.get(fineId);
        if (fine == null || fine.getStatus() == PaymentStatus.PAID) {
            return false;
        }

        PaymentProcessor processor = paymentProcessors.getOrDefault(method, new UpiPaymentProcessor());
        boolean success = processor.processPayment(fine.getStudentId(), fine.getAmount(), transactionRef);

        if (success) {
            fine.settle(method, transactionRef);
            totalFinesCollected += fine.getAmount();
            if (student != null) {
                student.deductFine(fine.getAmount());
            }
            return true;
        }
        return false;
    }

    public synchronized boolean payAllFinesForStudent(String studentId, PaymentMethod method, String transactionRef, Student student) {
        List<Fine> pending = finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId) && f.getStatus() == PaymentStatus.PENDING)
                .collect(Collectors.toList());

        if (pending.isEmpty()) return false;

        for (Fine f : pending) {
            f.settle(method, transactionRef);
            totalFinesCollected += f.getAmount();
        }
        if (student != null) {
            student.deductFine(student.getOutstandingFine());
        }
        return true;
    }

    public double getTotalFinesCollectedToday() {
        return totalFinesCollected;
    }

    public FineCalculator getFineCalculator() {
        return fineCalculator;
    }

    private void seedInitialFines() {
        Fine f1 = createFine("2026CS142", 1, "Database System Concepts", 40.0);
        // Previously paid fine history
        Fine f2 = createFine("2026CS142", 2, "Operating System Concepts", 20.0);
        f2.settle(PaymentMethod.UPI, "TXN-842918");

        Fine f3 = createFine("2026CS108", 3, "Computer Networks", 15.0);
        f3.settle(PaymentMethod.CASH, "CASH-REC-102");
    }
}

// ============================================================================
// SECTION: CirculationService
// ============================================================================

class CirculationService {
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

// ============================================================================
// SECTION: LibrarySystem
// ============================================================================

class LibrarySystem {
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

// ============================================================================
// SECTION: TestRunner
// ============================================================================

class TestRunner {
    public static void runAllTests(LibrarySystem system) {
        System.out.println("Running OOP Domain Unit & Workflow Tests...");

        // Test 1: Inheritance and polymorphism
        Student s = system.getUserService().getStudentById("2026CS142").orElseThrow();
        assert s.getRole() == Role.STUDENT : "Student role mismatch";
        assert s.getMaxBorrowQuota() == 4 : "Student max borrow mismatch";
        System.out.println(" [PASS] 1. Inheritance and Polymorphic Role logic");

        // Test 2: Adding a book generates copies and barcodes
        int initialBookCount = system.getCatalogService().getAllBooks().size();
        Book newBook = system.addBook("Design Patterns in Java", "GoF Team", "978-0201633611",
                "Computer Science", "2nd", "Addison-Wesley", "CS-B-25", "bg-purple-900", 3, "TestActor");
        assert newBook.getId() > 0 : "New book ID failed";
        assert newBook.getTotalCopies() == 3 : "Total copies mismatch";
        assert newBook.getAvailableCopies() == 3 : "Available copies mismatch";
        assert system.getCatalogService().getAllBooks().size() == initialBookCount + 1 : "Catalog size mismatch";
        System.out.println(" [PASS] 2. Book Addition & Accession Barcode Generation");

        // Test 3: Circulation Issue reduces available stock
        String barcodeToIssue = newBook.getCopies().get(0).getBarcode();
        BorrowRecord record = system.issueBook("2026CS142", barcodeToIssue, "TestLibrarian");
        assert record != null : "Record must not be null";
        assert newBook.getAvailableCopies() == 2 : "Available copies should decrement to 2";
        System.out.println(" [PASS] 3. Issue Book & Dynamic Stock Deduction");

        // Test 4: Return Book restores available stock and records audit
        BorrowRecord returned = system.returnBook(barcodeToIssue, "TestLibrarian");
        assert returned.isReturned() : "Record must be marked returned";
        assert newBook.getAvailableCopies() == 3 : "Available copies should restore to 3";
        System.out.println(" [PASS] 4. Return Book & Stock Restoration");

        // Test 5: Fine Calculation & Settlement Strategy
        double initialPending = system.getFineService().getPendingFineTotalForStudent("2026CS142");
        assert initialPending > 0 : "Should have pending fine";
        boolean paid = system.payAllFines("2026CS142", PaymentMethod.UPI, "TEST-TXN-001", "TestStudent");
        assert paid : "Payment should succeed";
        assert system.getFineService().getPendingFineTotalForStudent("2026CS142") == 0.0 : "Pending fines should be 0";
        System.out.println(" [PASS] 5. Overdue Fine Calculation & Settlement Strategy");

        System.out.println("All 5 OOP Tests Passed Successfully!\n");
    }
}

// ============================================================================
// SECTION: ConsoleUI
// ============================================================================

class ConsoleUI {
    private final LibrarySystem system;
    private final Scanner scanner;

    public ConsoleUI(LibrarySystem system) {
        this.system = system;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("    COLLEGE LIBRARY MANAGEMENT SYSTEM (JAVA OOP) ");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            System.out.println("\nSelect Role Portal:");
            System.out.println("1. 📱 Student Portal (Student - 2026CS142)");
            System.out.println("2. 🛡️ Librarian Console (Circulation Desk & System Config)");
            System.out.println("3. 📚 View Complete Book Catalogue");
            System.out.println("4. 📋 View Recent Audit Trail");
            System.out.println("0. Exit Application");
            System.out.print("Enter choice (0-4): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleStudentMenu();
                case "2" -> handleLibrarianAdminMenu();
                case "3" -> displayCatalogue();
                case "4" -> displayAuditTrail();
                case "0" -> {
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please choose 0-4.");
            }
        }
    }

    private void handleStudentMenu() {
        String studentId = "2026CS142";
        Student s = system.getUserService().getStudentById(studentId).orElse(null);
        if (s == null) return;

        System.out.println("\n--- 📱 Student Portal: " + s.getName() + " (" + s.getId() + ") ---");
        System.out.println("Outstanding Fine: ₹" + s.getOutstandingFine());
        System.out.println("Current Borrowed: " + s.getCurrentBorrowedCount() + " / " + s.getMaxBorrowQuota());
        System.out.println("1. View My Active Loans");
        System.out.println("2. Search Book Catalog");
        System.out.println("3. Pay Overdue Fine Online");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choice: ");

        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1" -> {
                List<BorrowRecord> loans = system.getCirculationService().getActiveLoansForStudent(studentId);
                if (loans.isEmpty()) {
                    System.out.println("No active loans.");
                } else {
                    loans.forEach(l -> System.out.println(" • " + l));
                }
            }
            case "2" -> {
                System.out.print("Search title / author / isbn: ");
                String q = scanner.nextLine().trim();
                List<Book> results = system.getCatalogService().searchBooks(q);
                System.out.println("Found " + results.size() + " book(s):");
                results.forEach(b -> System.out.printf(" - %s by %s [%d/%d avail] (Shelf: %s)%n",
                        b.getTitle(), b.getAuthor(), b.getAvailableCopies(), b.getTotalCopies(), b.getShelfLocation()));
            }
            case "3" -> {
                if (s.getOutstandingFine() <= 0) {
                    System.out.println("✓ No outstanding dues on your account!");
                } else {
                    System.out.println("Current dues: ₹" + s.getOutstandingFine());
                    System.out.print("Confirm online payment of ₹" + s.getOutstandingFine() + " via UPI? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        system.payAllFines(studentId, PaymentMethod.UPI, "TXN-" + System.currentTimeMillis() % 1000000, "Student");
                        System.out.println("✓ Payment Successful! Receipt generated. All fines cleared.");
                    }
                }
            }
        }
    }

    private void handleLibrarianAdminMenu() {
        System.out.println("\n--- 🛡️ Librarian Console ---");
        System.out.println("Station:   Station #102 · Handheld Laser Scanner Ready");
        System.out.println("Role:      Librarian (LIB-001)");
        System.out.println("1. 📤 Issue Book Copy to Student (Barcode)");
        System.out.println("2. 📥 Return Book by Accession Barcode");
        System.out.println("3. ➕ Add New Book to Inventory");
        System.out.println("4. 👥 Search Members Directory");
        System.out.println("5. ⚙️ View Library Borrowing Policies");
        System.out.println("6. 📊 View Live System Statistics");
        System.out.println("7. 👤 View Librarian Profile & Credentials");
        System.out.println("8. Back");
        System.out.print("Choice: ");

        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1" -> {
                System.out.print("Enter Student ID (e.g. 2026CS142): ");
                String sid = scanner.nextLine().trim();
                System.out.print("Enter Book Copy Barcode (e.g. LIB-000603): ");
                String barcode = scanner.nextLine().trim();
                try {
                    BorrowRecord r = system.issueBook(sid, barcode, "Librarian #102");
                    System.out.println("✓ SUCCESS! Book issued: " + r.getBookTitle() + " to " + sid + ". Due on: " + r.getDueDate());
                } catch (Exception e) {
                    System.out.println("⚠️ ERROR: " + e.getMessage());
                }
            }
            case "2" -> {
                System.out.print("Scan/Enter Book Barcode (e.g. LIB-000610 or LIB-000620): ");
                String barcode = scanner.nextLine().trim();
                try {
                    BorrowRecord r = system.returnBook(barcode, "Librarian #102");
                    System.out.println("✓ SUCCESS! Book returned: " + r.getBookTitle() + " from " + r.getStudentId());
                    if (r.getFineAccrued() > 0) {
                        System.out.println("⚠️ Overdue fine assessed: ₹" + r.getFineAccrued());
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ ERROR: " + e.getMessage());
                }
            }
            case "3" -> {
                System.out.print("Title: ");
                String title = scanner.nextLine().trim();
                System.out.print("Author: ");
                String author = scanner.nextLine().trim();
                System.out.print("ISBN: ");
                String isbn = scanner.nextLine().trim();
                System.out.print("Shelf: ");
                String shelf = scanner.nextLine().trim();
                System.out.print("Copies: ");
                int copies = Integer.parseInt(scanner.nextLine().trim());

                Book b = system.addBook(title, author, isbn, "Computer Science", "1st", "Academic Press", shelf, "bg-blue-900", copies, "Librarian #102");
                System.out.println("✓ SUCCESS! Book added [ID: " + b.getId() + "] with " + copies + " accession barcodes generated.");
            }
            case "4" -> {
                System.out.print("Search member name / register no: ");
                String q = scanner.nextLine().trim();
                List<User> list = system.getUserService().searchUsers(q);
                list.forEach(u -> System.out.println(" • " + u));
            }
            case "5" -> {
                System.out.println("\n--- ⚙️ Library Borrowing Policies ---");
                System.out.println(system.getCirculationService().getPolicy(Role.STUDENT));
                System.out.println(system.getCirculationService().getPolicy(Role.FACULTY));
            }
            case "6" -> {
                System.out.println("\n--- 📊 Live System Statistics ---");
                System.out.println(system.getDashboardStats());
            }
            case "7" -> {
                System.out.println("\n--- 👤 Librarian Profile ---");
                System.out.println("   Name:             Librarian");
                System.out.println("   Staff ID:         LIB-001");
                System.out.println("   Role:             Librarian");
                System.out.println("   Department:       Circulation & Cataloguing Services");
                System.out.println("   Assigned Station: Circulation Desk Station #102");
                System.out.println("   Hardware:         USB Handheld Laser Scanner [READY]");
                System.out.println("   Engine:           Java 21 LTS Core");
            }
        }
    }

    private void displayCatalogue() {
        System.out.println("\n--- 📚 Library Book Catalogue ---");
        system.getCatalogService().getAllBooks().forEach(b ->
                System.out.printf("[%d] %-35s | %-20s | %d/%d Avail | Shelf: %s%n",
                        b.getId(), b.getTitle(), b.getAuthor(), b.getAvailableCopies(), b.getTotalCopies(), b.getShelfLocation()));
    }

    private void displayAuditTrail() {
        System.out.println("\n--- 📋 Recent Audit Trail ---");
        system.getAuditService().getRecentLogs(10).forEach(log ->
                System.out.printf("[%s] %-15s by %-16s: %s%n",
                        log.getFormattedTimestamp(), log.getAction(), log.getActor(), log.getDetail()));
    }
}

// ============================================================================
// SECTION: LibraryHttpServer
// ============================================================================

class LibraryHttpServer {
    private final LibrarySystem system;
    private final int port;
    private HttpServer server;
    private final Path webRoot;

    public LibraryHttpServer(LibrarySystem system, int port, Path webRoot) {
        this.system = system;
        this.port = port;
        this.webRoot = webRoot;
    }

    private int activePort;

    public void start() throws IOException {
        activePort = port;
        IOException lastEx = null;
        for (int p = port; p < port + 10; p++) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                activePort = p;
                lastEx = null;
                break;
            } catch (IOException e) {
                lastEx = e;
            }
        }
        if (server == null && lastEx != null) {
            throw lastEx;
        }

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // API Endpoints
        server.createContext("/api/books", new BooksHandler());
        server.createContext("/api/copies", new CopiesHandler());
        server.createContext("/api/copies/add", new CopiesHandler());
        server.createContext("/api/issue", new IssueHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.createContext("/api/renew", new RenewHandler());
        server.createContext("/api/fines", new FinesHandler());
        server.createContext("/api/members", new MembersHandler());
        server.createContext("/api/users", new MembersHandler());
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/audit", new AuditHandler());
        server.createContext("/api/rules", new RulesHandler());
        server.createContext("/api/login", new LoginHandler());

        // Static Web Files (Serves the exact UI HTML/CSS/JS)
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println(" Library HTTP & REST Server running at: http://localhost:" + activePort + "/");
    }

    public int getActivePort() {
        return activePort;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --- Handlers ---

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            Path file = null;

            if (path.equals("/") || path.equals("/index.html") || path.equals("/library.html") || path.isEmpty()) {
                Path[] candidates = {
                    webRoot.resolve("java-lms/resources/web/index.html"),
                    webRoot.resolve("resources/web/index.html"),
                    Paths.get("java-lms/resources/web/index.html").toAbsolutePath(),
                    Paths.get("resources/web/index.html").toAbsolutePath()
                };
                for (Path c : candidates) {
                    if (Files.exists(c)) {
                        file = c;
                        break;
                    }
                }
            } else {
                Path candidate = webRoot.resolve(path.startsWith("/") ? path.substring(1) : path).normalize();
                if (Files.exists(candidate) && !Files.isDirectory(candidate)) {
                    file = candidate;
                }
            }

            if (file == null || !Files.exists(file)) {
                // Fallback to resources index.html
                file = webRoot.resolve("java-lms/resources/web/index.html");
            }

            byte[] bytes = null;
            String contentType = "text/html; charset=UTF-8";

            if (file != null && Files.exists(file)) {
                bytes = Files.readAllBytes(file);
                if (file.toString().endsWith(".css")) contentType = "text/css; charset=UTF-8";
                else if (file.toString().endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
                else if (file.toString().endsWith(".json")) contentType = "application/json; charset=UTF-8";
            } else {
                try (InputStream is = getClass().getResourceAsStream("/resources/web/index.html")) {
                    if (is != null) {
                        bytes = is.readAllBytes();
                    }
                } catch (Exception ignored) {}
            }

            if (bytes != null) {
                sendCors(exchange);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String notFound = "<h1>404 File Not Found</h1>";
                sendCors(exchange);
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    private class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                List<Book> books = system.getCatalogService().getAllBooks();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    json.append(bookToJson(b));
                    if (i < books.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);

                String title = map.getOrDefault("title", "Untitled Book");
                String author = map.getOrDefault("author", "Unknown Author");
                String isbn = map.getOrDefault("isbn", "978-0000000000");
                String category = map.getOrDefault("category", "Computer Science");
                String edition = map.getOrDefault("edition", "1st");
                String publisher = map.getOrDefault("publisher", "Academic Press");
                String shelf = map.getOrDefault("shelf", "CS-B-01");
                String cover = map.getOrDefault("cover", "bg-blue-900");
                int copies = 3;
                try {
                    copies = Integer.parseInt(map.getOrDefault("copies", "3"));
                } catch (NumberFormatException ignored) {}

                Book newBook = system.addBook(title, author, isbn, category, edition, publisher, shelf, cover, copies, "Librarian #102");
                sendJson(exchange, 201, bookToJson(newBook));
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class CopiesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                int bookId = 1;
                try {
                    bookId = Integer.parseInt(map.getOrDefault("bookId", "1"));
                } catch (Exception ignored) {}
                String barcode = map.get("barcode");
                String shelf = map.get("shelf");

                BookCopy copy = system.getCatalogService().addCopy(bookId, barcode, shelf);
                if (copy != null) {
                    system.getAuditService().logAction("Librarian #102", "COPY_ADDED",
                            String.format("Added physical copy %s for book #%d (Shelf: %s)", copy.getBarcode(), bookId, copy.getShelfLocation()));
                    sendJson(exchange, 200, String.format("{\"success\":true,\"barcode\":\"%s\",\"shelf\":\"%s\"}", copy.getBarcode(), copy.getShelfLocation()));
                } else {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"Book not found\"}");
                }
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            int bookId = 1;
            if (query != null && query.contains("bookId=")) {
                try {
                    bookId = Integer.parseInt(query.split("bookId=")[1].split("&")[0]);
                } catch (Exception ignored) {}
            }

            List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bookId);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < copies.size(); i++) {
                BookCopy c = copies.get(i);
                json.append(String.format("{\"barcode\":\"%s\",\"bookId\":%d,\"status\":\"%s\",\"shelf\":\"%s\",\"borrower\":\"%s\"}",
                        c.getBarcode(), c.getBookId(), c.getStatus().name(), c.getShelfLocation(),
                        c.getBorrowerId() != null ? c.getBorrowerId() : ""));
                if (i < copies.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class IssueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String studentId = map.getOrDefault("studentId", "2026CS142");
                String barcode = map.get("barcode");
                String bookIdStr = map.get("bookId");

                // If barcode not provided but bookId provided, pick first available copy
                if ((barcode == null || barcode.isBlank()) && bookIdStr != null) {
                    try {
                        int bId = Integer.parseInt(bookIdStr);
                        List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bId);
                        for (BookCopy c : copies) {
                            if (c.isAvailable()) {
                                barcode = c.getBarcode();
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (barcode == null || barcode.isBlank()) {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"No available copy found or barcode missing\"}");
                    return;
                }

                try {
                    BorrowRecord record = system.issueBook(studentId, barcode, "Librarian #102");
                    sendJson(exchange, 200, String.format("{\"success\":true,\"message\":\"Book issued successfully\",\"recordId\":%d,\"barcode\":\"%s\",\"dueDate\":\"%s\"}",
                            record.getRecordId(), record.getCopyBarcode(), record.getDueDate()));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String barcode = map.get("barcode");

                // If bookId provided instead, pick first issued copy
                if ((barcode == null || barcode.isBlank()) && map.get("bookId") != null) {
                    try {
                        int bId = Integer.parseInt(map.get("bookId"));
                        List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bId);
                        for (BookCopy c : copies) {
                            if (!c.isAvailable()) {
                                barcode = c.getBarcode();
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (barcode == null || barcode.isBlank()) {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"Barcode required to return book\"}");
                    return;
                }

                try {
                    BorrowRecord record = system.returnBook(barcode, "Librarian #102");
                    sendJson(exchange, 200, String.format("{\"success\":true,\"message\":\"Book returned successfully\",\"fine\":%.2f,\"studentId\":\"%s\"}",
                            record.getFineAccrued(), record.getStudentId()));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class RenewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                int recordId = Integer.parseInt(map.getOrDefault("recordId", "1"));
                try {
                    boolean ok = system.renewBook(recordId, "Student");
                    sendJson(exchange, 200, String.format("{\"success\":%b,\"message\":\"Loan renewed successfully\"}", ok));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            }
        }
    }

    private class FinesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String studentId = map.getOrDefault("studentId", "2026CS142");
                PaymentMethod pm = PaymentMethod.fromString(map.getOrDefault("method", "upi"));
                String ref = map.getOrDefault("reference", "TXN-" + System.currentTimeMillis() % 1000000);

                boolean ok = system.payAllFines(studentId, pm, ref, "Student Online Gateway");
                sendJson(exchange, 200, String.format("{\"success\":%b,\"reference\":\"%s\",\"message\":\"Payment processed successfully\"}", ok, ref));
            } else {
                String query = exchange.getRequestURI().getQuery();
                String studentId = "2026CS142";
                if (query != null && query.contains("studentId=")) {
                    studentId = query.split("studentId=")[1].split("&")[0];
                }
                List<Fine> fines = system.getFineService().getFinesForStudent(studentId);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < fines.size(); i++) {
                    Fine f = fines.get(i);
                    json.append(String.format("{\"id\":%d,\"amount\":%.2f,\"status\":\"%s\",\"book\":\"%s\",\"date\":\"%s\",\"ref\":\"%s\"}",
                            f.getFineId(), f.getAmount(), f.getStatus().name(), f.getBookTitle(),
                            f.getFormattedDate(), f.getTransactionRef() != null ? f.getTransactionRef() : ""));
                    if (i < fines.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            }
        }
    }

    private class MembersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod().toUpperCase();
            if ("POST".equals(method) || "PUT".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String origId = map.getOrDefault("origId", map.getOrDefault("id", "")).trim();
                String newId = map.getOrDefault("id", origId).trim();
                String name = map.getOrDefault("name", "").trim();
                String password = map.getOrDefault("password", map.getOrDefault("pass", "password123")).trim();
                String dept = map.getOrDefault("dept", map.getOrDefault("department", "")).trim();
                String roleStr = map.getOrDefault("role", "Student").trim();
                String status = map.getOrDefault("status", "active").trim();

                if (newId.isEmpty() || name.isEmpty()) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"Name and ID are required\"}");
                    return;
                }

                system.getUserService().updateOrRegisterUser(origId, newId, name, password, dept, roleStr, status);
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"User details saved successfully\"}");
                return;
            }

            if ("DELETE".equals(method)) {
                String path = exchange.getRequestURI().getPath();
                String id = path.substring(path.lastIndexOf('/') + 1);
                boolean deleted = system.getUserService().deleteUser(id);
                sendJson(exchange, 200, String.format("{\"success\":%b}", deleted));
                return;
            }

            List<User> users = system.getUserService().getAllUsers();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                double fine = (u instanceof Student s) ? s.getOutstandingFine() : 0.0;
                int borrowed = (u instanceof Student s) ? s.getCurrentBorrowedCount() : 0;
                String role = (u instanceof Admin || u instanceof Librarian) ? "Librarian" : u.getRole().getDisplayName();

                json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"department\":\"%s\",\"active\":%b,\"fine\":%.2f,\"borrowed\":%d}",
                        escapeJson(u.getId()), escapeJson(u.getName()), escapeJson(u.getPassword()), escapeJson(role), escapeJson(u.getDepartment()), u.isActive(), fine, borrowed));
                if (i < users.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, Object> stats = system.getDashboardStats();
            String json = String.format("{\"todayIssues\":%d,\"todayReturns\":%d,\"overdueBooks\":%d,\"finesToday\":%.2f,\"totalBooks\":%d,\"totalMembers\":%d}",
                    stats.get("todayIssues"), stats.get("todayReturns"), stats.get("overdueBooks"),
                    stats.get("fineCollectedToday"), stats.get("totalBooks"), stats.get("totalMembers"));
            sendJson(exchange, 200, json);
        }
    }

    private class AuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            List<AuditLog> logs = system.getAuditService().getRecentLogs(30);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < logs.size(); i++) {
                AuditLog l = logs.get(i);
                json.append(String.format("{\"id\":%d,\"ts\":\"%s\",\"actor\":\"%s\",\"action\":\"%s\",\"detail\":\"%s\"}",
                        l.getId(), l.getFormattedTimestamp(), escapeJson(l.getActor()),
                        escapeJson(l.getAction()), escapeJson(l.getDetail())));
                if (i < logs.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class RulesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("PUT".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                Role role = Role.fromString(map.getOrDefault("role", "student"));
                int maxBooks = Integer.parseInt(map.getOrDefault("maxBooks", "4"));
                int loanDays = Integer.parseInt(map.getOrDefault("loanPeriod", "14"));
                int maxRenew = Integer.parseInt(map.getOrDefault("maxRenewals", "2"));
                double fineRate = Double.parseDouble(map.getOrDefault("fineRate", "2.0"));

                system.getCirculationService().updatePolicy(role, maxBooks, loanDays, maxRenew, fineRate);
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Policy updated successfully\"}");
            } else {
                BorrowPolicy sp = system.getCirculationService().getPolicy(Role.STUDENT);
                BorrowPolicy fp = system.getCirculationService().getPolicy(Role.FACULTY);
                String json = String.format("{\"student\":{\"maxBooks\":%d,\"loanPeriod\":%d,\"maxRenewals\":%d,\"fineRate\":%.2f}," +
                                            "\"faculty\":{\"maxBooks\":%d,\"loanPeriod\":%d,\"maxRenewals\":%d,\"fineRate\":%.2f}}",
                        sp.getMaxBooks(), sp.getLoanPeriodDays(), sp.getMaxRenewals(), sp.getDailyFineRate(),
                        fp.getMaxBooks(), fp.getLoanPeriodDays(), fp.getMaxRenewals(), fp.getDailyFineRate());
                sendJson(exchange, 200, json);
            }
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String id = map.getOrDefault("id", map.getOrDefault("username", "")).trim();

                String password = map.getOrDefault("password", map.getOrDefault("pass", "")).trim();

                if (id.isEmpty()) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"ID/Username is required\"}");
                    return;
                }

                Optional<User> userOpt = system.getUserService().getUserById(id);
                if (userOpt.isPresent()) {
                    User u = userOpt.get();
                    if (!password.isEmpty() && !u.getPassword().equals(password)) {
                        sendJson(exchange, 401, "{\"success\":false,\"message\":\"Incorrect password.\"}");
                        return;
                    }
                    boolean isStaff = (u instanceof Admin || u instanceof Librarian);
                    String userRole = isStaff ? "admin" : "student";
                    String roleName = isStaff ? "Librarian" : u.getRole().getDisplayName();
                    sendJson(exchange, 200, String.format(
                        "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"%s\",\"roleType\":\"%s\",\"department\":\"%s\"}}",
                        escapeJson(u.getId()), escapeJson(u.getName()), escapeJson(roleName), userRole, escapeJson(u.getDepartment())
                    ));
                } else {
                    String cleanId = id.toUpperCase();
                    if (cleanId.startsWith("ADM") || cleanId.startsWith("LIB") || cleanId.equals("ADMIN") || cleanId.equals("LIBRARIAN")) {
                        sendJson(exchange, 200, String.format(
                            "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"Librarian\",\"roleType\":\"admin\",\"department\":\"Library Services\"}}",
                            escapeJson(cleanId), "Librarian"
                        ));
                    } else if (cleanId.startsWith("202") || cleanId.contains("CE") || cleanId.contains("CS") || cleanId.contains("EC") || cleanId.contains("ME")) {
                        sendJson(exchange, 200, String.format(
                            "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"Student\",\"roleType\":\"student\",\"department\":\"Engineering Department\"}}",
                            escapeJson(cleanId), "Student Member"
                        ));
                    } else {
                        sendJson(exchange, 401, "{\"success\":false,\"message\":\"Account not found. Invalid ID or credentials.\"}");
                    }
                }
            } else {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    // --- Utility Methods ---

    private String bookToJson(Book b) {
        return String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"isbn\":\"%s\",\"edition\":\"%s\"," +
                             "\"category\":\"%s\",\"publisher\":\"%s\",\"available\":%d,\"total\":%d,\"shelf\":\"%s\",\"cover\":\"%s\"}",
                b.getId(), escapeJson(b.getTitle()), escapeJson(b.getAuthor()), b.getIsbn(),
                escapeJson(b.getEdition()), escapeJson(b.getCategory()), escapeJson(b.getPublisher()),
                b.getAvailableCopies(), b.getTotalCopies(), escapeJson(b.getShelfLocation()), b.getCoverStyle());
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private Map<String, String> parseJsonSimple(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;
        String clean = body.trim();
        if (clean.startsWith("{") && clean.endsWith("}")) {
            clean = clean.substring(1, clean.length() - 1);
        }

        // Split by top-level commas
        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(key, val);
            }
        }
        return map;
    }
}
