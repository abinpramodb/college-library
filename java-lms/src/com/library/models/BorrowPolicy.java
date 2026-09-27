package com.library.models;

import com.library.enums.Role;

public class BorrowPolicy {
    private Role role;
    private int maxBooks;
    private int loanPeriodDays;
    private int maxRenewals;
    private double dailyFineRate;

    public BorrowPolicy(Role role, int maxBooks, int loanPeriodDays, int maxRenewals, double dailyFineRate) {
        this.role = role;
        this.maxBooks = maxBooks;
        this.loanPeriodDays = loanPeriodDays;
        this.maxRenewals = maxRenewals;
        this.dailyFineRate = dailyFineRate;
    }

    public Role getRole() {
        return role;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    public int getMaxRenewals() {
        return maxRenewals;
    }

    public void setMaxRenewals(int maxRenewals) {
        this.maxRenewals = maxRenewals;
    }

    public double getDailyFineRate() {
        return dailyFineRate;
    }

    public void setDailyFineRate(double dailyFineRate) {
        this.dailyFineRate = dailyFineRate;
    }

    @Override
    public String toString() {
        return String.format("Policy for %s: Max %d books, %d days loan, %d renewals, ₹%.2f/day fine",
                role.getDisplayName(), maxBooks, loanPeriodDays, maxRenewals, dailyFineRate);
    }
}
