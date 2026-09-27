package com.library.ui;

import com.library.LibrarySystem;
import com.library.enums.PaymentMethod;
import com.library.enums.Role;
import com.library.models.Book;
import com.library.models.BorrowRecord;
import com.library.models.Student;

public class TestRunner {
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
