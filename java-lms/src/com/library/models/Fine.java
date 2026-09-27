package com.library.models;

import com.library.enums.PaymentMethod;
import com.library.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Fine {
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
