package com.library.enums;

public enum BookStatus {
    AVAILABLE("Available in Stacks", true),
    ISSUED("Checked Out", false),
    RESERVED("On Hold for Reservation", false),
    DAMAGED("Under Maintenance / Repair", false),
    LOST("Reported Lost", false);

    private final String description;
    private final boolean canBorrow;

    BookStatus(String description, boolean canBorrow) {
        this.description = description;
        this.canBorrow = canBorrow;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBorrowable() {
        return canBorrow;
    }
}
