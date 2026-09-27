package com.library.ui;

import com.library.LibrarySystem;
import com.library.enums.PaymentMethod;
import com.library.models.Book;
import com.library.models.BorrowRecord;
import com.library.models.Student;
import com.library.models.User;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
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
                System.out.println(system.getCirculationService().getPolicy(com.library.enums.Role.STUDENT));
                System.out.println(system.getCirculationService().getPolicy(com.library.enums.Role.FACULTY));
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
