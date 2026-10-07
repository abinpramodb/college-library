import java.util.*;

// ==============================================================================
// 1. DOMAIN MODELS (OOP: ENCAPSULATION & INHERITANCE)
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
        return String.format("[ID: %d] %-30s by %-20s | Status: %s", id, title, author, status);
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
// 2. LIBRARY SYSTEM CONTROLLER
// ==============================================================================

class Library {
    private List<Book> books = new ArrayList<>();
    private Map<String, Student> students = new HashMap<>();
    private int nextBookId = 101;

    public Library() {
        // Sample Books
        addBook("Clean Code", "Robert C. Martin");
        addBook("Introduction to Algorithms", "Thomas H. Cormen");
        addBook("Operating System Concepts", "Abraham Silberschatz");
        addBook("Design Patterns", "Erich Gamma");
        addBook("Computer Networks", "Andrew Tanenbaum");

        // Sample Students
        students.put("CS001", new Student("CS001", "Rahul Sharma"));
        students.put("EC014", new Student("EC014", "Ananya Verma"));
        students.put("CS142", new Student("CS142", "Kiran Joseph"));
    }

    public void addBook(String title, String author) {
        books.add(new Book(nextBookId++, title, author));
    }

    public void displayAllBooks() {
        System.out.println("\n----------------- 📚 ALL BOOKS IN LIBRARY -----------------");
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        for (Book b : books) {
            System.out.println(b);
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
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("❌ Error: Student ID '" + studentId + "' not registered.");
            return;
        }

        if (student.getBorrowedCount() >= 3) {
            System.out.println("❌ Error: Student has already reached the maximum borrow limit (3 books).");
            return;
        }

        for (Book b : books) {
            if (b.getId() == bookId) {
                if (b.isIssued()) {
                    System.out.println("❌ Error: Book is already issued to " + b.getIssuedTo());
                    return;
                }
                b.issueTo(studentId);
                student.incrementBorrowed();
                System.out.println("✓ Success: '" + b.getTitle() + "' successfully issued to " + student.getName() + " (" + studentId + ")!");
                return;
            }
        }
        System.out.println("❌ Error: Book ID " + bookId + " not found.");
    }

    public void returnBook(int bookId) {
        for (Book b : books) {
            if (b.getId() == bookId) {
                if (!b.isIssued()) {
                    System.out.println("⚠️ Warning: This book is already in the library (not issued).");
                    return;
                }
                String studentId = b.getIssuedTo();
                Student s = students.get(studentId);
                if (s != null) s.decrementBorrowed();

                b.returnBook();
                System.out.println("✓ Success: '" + b.getTitle() + "' has been returned to the library!");
                return;
            }
        }
        System.out.println("❌ Error: Book ID " + bookId + " not found.");
    }
}

// ==============================================================================
// 3. MAIN INTERACTIVE TERMINAL APP
// ==============================================================================

public class SimpleLibrary {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library library = new Library();

        System.out.println("==================================================");
        System.out.println("      📚 COLLEGE LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n------------- MENU -------------");
            System.out.println("1. View All Books");
            System.out.println("2. Search Book");
            System.out.println("3. Issue Book to Student");
            System.out.println("4. Return Book");
            System.out.println("5. Add New Book");
            System.out.println("0. Exit");
            System.out.print("Enter choice (0-5): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> library.displayAllBooks();

                case "2" -> {
                    System.out.print("Enter title or author to search: ");
                    String q = scanner.nextLine().trim();
                    library.searchBook(q);
                }

                case "3" -> {
                    System.out.print("Enter Book ID (e.g. 101): ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Enter Student ID (e.g. CS001, EC014, CS142): ");
                        String sid = scanner.nextLine().trim();
                        library.issueBook(bid, sid);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID. Must be a number.");
                    }
                }

                case "4" -> {
                    System.out.print("Enter Book ID to return (e.g. 101): ");
                    try {
                        int bid = Integer.parseInt(scanner.nextLine().trim());
                        library.returnBook(bid);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid Book ID. Must be a number.");
                    }
                }

                case "5" -> {
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

                case "0" -> {
                    System.out.println("\n✓ Thank you for using the Library System. Goodbye!");
                    running = false;
                }

                default -> System.out.println("❌ Invalid choice. Please choose 0 to 5.");
            }
        }
        scanner.close();
    }
}
