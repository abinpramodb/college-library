package com.library.enums;

public enum PaymentStatus {
    PENDING("Payment Outstanding"),
    PAID("Settled & Cleared"),
    WAIVED("Waived by Authority");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
