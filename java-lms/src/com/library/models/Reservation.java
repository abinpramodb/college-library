package com.library.models;

import java.time.LocalDate;

public class Reservation {
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
