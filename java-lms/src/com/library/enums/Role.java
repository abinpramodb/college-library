package com.library.enums;

public enum Role {
    STUDENT("Student", "Undergraduate & Postgraduate Members"),
    LIBRARIAN("Librarian", "Circulation & Catalogue Operations"),
    FACULTY("Faculty", "Teaching & Research Staff"),
    ADMIN("Administrator", "System Configuration & Audit Management");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String text) {
        if (text == null) return STUDENT;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text) || r.displayName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return STUDENT;
    }
}
