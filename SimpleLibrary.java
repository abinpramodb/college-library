import java.util.*;

// ==============================================================================
// 1. MAIN APPLICATION (MUST BE FIRST CLASS FOR 'java SimpleLibrary.java')
// ==============================================================================

public class SimpleLibrary {
    private static Scanner scanner = new Scanner(System.in);
    private static Library library = new Library();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      📚 COLLEGE LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n------------- SELECT ROLE -------------");
            System.out.println("1. 📱 Student Portal");
            System.out.println("2. 🛡️  Librarian Console");
            System.out.println("0. 🚪 Exit");
            System.out.print("Enter choice (0-2): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> handleStudentPortal();
                case "2" -> handleLibrarianConsole();
                case "0" -> {
                    System.out.println("\n✓ Thank you for using the Library System. Goodbye!\n");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice. Please choose 0, 1, or 2.");
            }
        }
        scanner.close();
    }

    // --------------------------------------------------------------------------
    // 📱 STUDENT PORTAL WORKFLOWS
    // --------------------------------------------------------------------------
    private static void handleStudentPortal() {
        System.out.println("\n--- 📱 STUDENT LOGIN ---");
        System.out.println("Available demo IDs: CS001, EC014, CS142");
        System.out.print("Enter your Student ID (or type 'new' to register): ");
        String sid = scanner.nextLine().trim().toUpperCase();

        if (sid.equalsIgnoreCase("NEW")) {
            System.out.print("Enter your new Student ID (e.g. CS205): ");
            sid = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter your Full Name: ");
            String name = scanner.nextLine().trim();
            library.registerStudent(sid, name);
            System.out.println("✓ Success: Registered as student " + name + " (" + sid + ")!");
        }

        Student student = library.getStudent(sid);
        if (student == null) {
            System.out.println("❌ Error: Student ID '" + sid + "' not found.");
            return;
        }

        boolean inStudent = true;
        while (inStudent) {
            System.out.println("\n" + "=".repeat(45));
            System.out.println("📱 STUDENT: " + student.getName() + " (" + student.getId() + ")");
            System.out.println("   Active Loans: " + student.getBorrowedCount() + " / 3 books");
            System.out.println("=".repeat(45));
            System.out.println("1. View My Borrowed Books");
            System.out.println("2. Search & Browse Available Books");
            System.out.println("3. Borrow a Book");
            System.out.println("4. Return a Book");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice (0-4): ");

            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1" -> library.displayStudentLoans(student.getId());

                case "2" -> {
                    System.out.print("Enter title or author to search: ");
                    String q = scanner.nextLine().trim();
                    library.searchBook(q);
                }

                case "3" -> {
                    library.displayAvailableBooks();
                    System.out.print("Enter Book ID to borrow: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        library.issueBook(bid, student.getId());
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID.");
                    }
                }

                case "4" -> {
                    library.displayStudentLoans(student.getId());
                    System.out.print("Enter Book ID to return: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        library.returnBook(bid, student.getId());
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID.");
                    }
                }

                case "0" -> inStudent = false;
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    // --------------------------------------------------------------------------
    // 🛡️ LIBRARIAN CONSOLE WORKFLOWS
    // --------------------------------------------------------------------------
    private static void handleLibrarianConsole() {
        boolean inLibrarian = true;
        while (inLibrarian) {
            System.out.println("\n" + "=".repeat(45));
            System.out.println("🛡️  LIBRARIAN CONSOLE");
            System.out.println("=".repeat(45));
            System.out.println("1. View All Books (Full Inventory)");
            System.out.println("2. Add New Book Title");
            System.out.println("3. Issue Book to Student");
            System.out.println("4. Process Book Return");
            System.out.println("5. View All Registered Students");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice (0-5): ");

            String ch = scanner.nextLine().trim();
            switch (ch) {
                case "1" -> library.displayAllBooks();

                case "2" -> {
                    System.out.print("Enter Book Title: ");
                    String title = scanner.nextLine().trim();
                    System.out.print("Enter Author Name: ");
                    String author = scanner.nextLine().trim();
                    if (!title.isEmpty() && !author.isEmpty()) {
                        library.addBook(title, author);
                        System.out.println("✓ Success: New book added to the catalogue!");
                    } else {
                        System.out.println("❌ Title and Author cannot be empty.");
                    }
                }

                case "3" -> {
                    System.out.print("Enter Book ID: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter Student ID: ");
                        String sid = scanner.nextLine().trim().toUpperCase();
                        library.issueBook(bid, sid);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID.");
                    }
                }

                case "4" -> {
                    System.out.print("Enter Book ID to return: ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        library.returnBook(bid, null);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID.");
                    }
                }

                case "5" -> library.displayAllStudents();

                case "0" -> inLibrarian = false;
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }
}

// ==============================================================================
// 2. DOMAIN MODELS (OOP: ENCAPSULATION & INHERITANCE)
// ==============================================================================

class Book {
    private int id;
    private String title;
    private String author;
    private boolean isIssued;
    private String issuedToStudentId;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
        this.issuedToStudentId = "";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isIssued() { return isIssued; }
    public String getIssuedTo() { return issuedToStudentId; }

    public void issueTo(String studentId) {
        this.isIssued = true;
        this.issuedToStudentId = studentId;
    }

    public void returnBook() {
        this.isIssued = false;
        this.issuedToStudentId = "";
    }

    @Override
    public String toString() {
        String status = isIssued ? "ISSUED to (" + issuedToStudentId + ")" : "AVAILABLE";
        return String.format("[ID: %d] %-30s by %-20s | %s", id, title, author, status);
    }
}

class User {
    protected String id;
    protected String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}

class Student extends User {
    private int borrowedCount;

    public Student(String id, String name) {
        super(id, name);
        this.borrowedCount = 0;
    }

    public int getBorrowedCount() { return borrowedCount; }
    public void incrementBorrowed() { borrowedCount++; }
    public void decrementBorrowed() { if (borrowedCount > 0) borrowedCount--; }
}

// ==============================================================================
// 3. LIBRARY CONTROLLER
// ==============================================================================

class Library {
    private List<Book> books = new ArrayList<>();
    private Map<String, Student> students = new LinkedHashMap<>();
    private int nextBookId = 101;

    public Library() {
        // Pre-loaded Sample Books
        addBook("Clean Code", "Robert C. Martin");
        addBook("Introduction to Algorithms", "Thomas H. Cormen");
        addBook("Operating System Concepts", "Abraham Silberschatz");
        addBook("Design Patterns", "Erich Gamma");
        addBook("Computer Networks", "Andrew Tanenbaum");

        // Pre-loaded Sample Students
        registerStudent("CS001", "Rahul Sharma");
        registerStudent("EC014", "Ananya Verma");
        registerStudent("CS142", "Kiran Joseph");
    }

    public void addBook(String title, String author) {
        books.add(new Book(nextBookId++, title, author));
    }

    public void registerStudent(String id, String name) {
        students.put(id.toUpperCase(), new Student(id.toUpperCase(), name));
    }

    public Student getStudent(String id) {
        return students.get(id.toUpperCase());
    }

    public void displayAllBooks() {
        System.out.println("\n----------------- 📚 ALL BOOKS IN CATALOGUE -----------------");
        for (Book b : books) {
            System.out.println(b);
        }
    }

    public void displayAvailableBooks() {
        System.out.println("\n----------------- 📚 AVAILABLE BOOKS -----------------");
        boolean found = false;
        for (Book b : books) {
            if (!b.isIssued()) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) System.out.println("No books currently available.");
    }

    public void displayStudentLoans(String studentId) {
        System.out.println("\n--- 📖 Current Books Borrowed by " + studentId + " ---");
        boolean hasBooks = false;
        for (Book b : books) {
            if (b.isIssued() && b.getIssuedTo().equalsIgnoreCase(studentId)) {
                System.out.println(" • [ID: " + b.getId() + "] " + b.getTitle() + " by " + b.getAuthor());
                hasBooks = true;
            }
        }
        if (!hasBooks) {
            System.out.println("You currently have no borrowed books.");
        }
    }

    public void displayAllStudents() {
        System.out.println("\n----------------- 👥 REGISTERED STUDENTS -----------------");
        for (Student s : students.values()) {
            System.out.printf(" • [%-6s] %-20s (Borrowed: %d / 3)%n", s.getId(), s.getName(), s.getBorrowedCount());
        }
    }

    public void searchBook(String query) {
        String q = query.toLowerCase();
        System.out.println("\n--- Search Results for: '" + query + "' ---");
        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(q) || b.getAuthor().toLowerCase().contains(q)) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    public void issueBook(int bookId, String studentId) {
        Student student = getStudent(studentId);
        if (student == null) {
            System.out.println("❌ Error: Student ID '" + studentId + "' not registered.");
            return;
        }

        if (student.getBorrowedCount() >= 3) {
            System.out.println("❌ Error: You have reached the borrow limit of 3 books. Return one first.");
            return;
        }

        for (Book b : books) {
            if (b.getId() == bookId) {
                if (b.isIssued()) {
                    System.out.println("❌ Error: Book is already issued to " + b.getIssuedTo());
                    return;
                }
                b.issueTo(student.getId());
                student.incrementBorrowed();
                System.out.println("✓ Success: '" + b.getTitle() + "' successfully issued to " + student.getName() + " (" + student.getId() + ")!");
                return;
            }
        }
        System.out.println("❌ Error: Book ID " + bookId + " not found.");
    }

    public void returnBook(int bookId, String expectedStudentId) {
        for (Book b : books) {
            if (b.getId() == bookId) {
                if (!b.isIssued()) {
                    System.out.println("⚠️ Warning: This book is not currently issued.");
                    return;
                }
                if (expectedStudentId != null && !b.getIssuedTo().equalsIgnoreCase(expectedStudentId)) {
                    System.out.println("❌ Error: This book was borrowed by " + b.getIssuedTo() + ", not by you.");
                    return;
                }

                String sid = b.getIssuedTo();
                Student s = getStudent(sid);
                if (s != null) s.decrementBorrowed();

                b.returnBook();
                System.out.println("✓ Success: '" + b.getTitle() + "' has been returned to the library!");
                return;
            }
        }
        System.out.println("❌ Error: Book ID " + bookId + " not found.");
    }
}
