package com.library.enums;

public enum TransactionType {
    ISSUE("BOOK_ISSUED"),
    RETURN("BOOK_RETURNED"),
    RENEW("LOAN_RENEWED"),
    FINE_PAID("FINE_PAID"),
    BOOK_ADDED("BOOK_ADDED"),
    RULE_UPDATED("RULE_UPDATED"),
    USER_REGISTERED("USER_REGISTERED");

    private final String logCode;

    TransactionType(String logCode) {
        this.logCode = logCode;
    }

    public String getLogCode() {
        return logCode;
    }
}
