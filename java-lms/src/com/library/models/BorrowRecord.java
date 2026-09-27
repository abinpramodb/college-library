package com.library.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
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
