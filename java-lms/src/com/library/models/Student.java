package com.library.models;

import com.library.enums.Role;

public class Student extends User {
    private String registerNumber;
    private String semester;
    private double outstandingFine;
    private int currentBorrowedCount;

    public Student(String id, String name, String email, String department, String semester) {
        super(id, name, email, Role.STUDENT, department);
        this.registerNumber = id;
        this.semester = semester;
        this.outstandingFine = 0.0;
        this.currentBorrowedCount = 0;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public double getOutstandingFine() {
        return outstandingFine;
    }

    public void addFine(double amount) {
        this.outstandingFine += amount;
    }

    public void deductFine(double amount) {
        this.outstandingFine = Math.max(0.0, this.outstandingFine - amount);
    }

    public int getCurrentBorrowedCount() {
        return currentBorrowedCount;
    }

    public void incrementBorrowed() {
        this.currentBorrowedCount++;
    }

    public void decrementBorrowed() {
        this.currentBorrowedCount = Math.max(0, this.currentBorrowedCount - 1);
    }

    @Override
    public int getMaxBorrowQuota() {
        return 4;
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }

    public boolean canBorrow() {
        return active && currentBorrowedCount < getMaxBorrowQuota() && outstandingFine < 100.0;
    }
}
