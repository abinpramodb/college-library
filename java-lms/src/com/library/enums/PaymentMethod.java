package com.library.enums;

public enum PaymentMethod {
    UPI("Instant UPI", "upi"),
    CASH("Cash at Librarian Desk", "cash"),
    CARD("Debit / Credit Card", "card"),
    NET_BANKING("Net Banking", "netbanking");

    private final String label;
    private final String code;

    PaymentMethod(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static PaymentMethod fromString(String val) {
        if (val == null) return UPI;
        for (PaymentMethod pm : values()) {
            if (pm.code.equalsIgnoreCase(val) || pm.name().equalsIgnoreCase(val)) {
                return pm;
            }
        }
        return UPI;
    }
}
