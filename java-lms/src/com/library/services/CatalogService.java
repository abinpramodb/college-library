package com.library.services;

import com.library.enums.BookStatus;
import com.library.models.Book;
import com.library.models.BookCopy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CatalogService {
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
