package com.library.models;

import com.library.enums.Role;

public class Librarian extends User {
    private String deskStation;
    private String shift;

    public Librarian(String id, String name, String email, String department, String deskStation) {
        super(id, name, email, Role.LIBRARIAN, department);
        this.deskStation = deskStation;
        this.shift = "Day";
    }

    public String getDeskStation() {
        return deskStation;
    }

    public void setDeskStation(String deskStation) {
        this.deskStation = deskStation;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 10;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }
}
