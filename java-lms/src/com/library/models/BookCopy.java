package com.library.models;

import com.library.enums.BookStatus;

public class BookCopy {
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
