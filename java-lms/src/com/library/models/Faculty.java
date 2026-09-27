package com.library.models;

import com.library.enums.Role;

public class Faculty extends User {
    private String designation;
    private String researchArea;

    public Faculty(String id, String name, String email, String department, String designation) {
        super(id, name, email, Role.FACULTY, department);
        this.designation = designation;
        this.researchArea = "Computer Science";
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getResearchArea() {
        return researchArea;
    }

    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 8;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }
}
