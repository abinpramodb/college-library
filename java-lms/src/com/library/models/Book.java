package com.library.models;

import com.library.interfaces.Searchable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Book implements Searchable {
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
