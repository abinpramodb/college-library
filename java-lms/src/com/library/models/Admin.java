package com.library.models;

import com.library.enums.Role;

public class Admin extends User {
    private int accessLevel;

    public Admin(String id, String name, String email, String department) {
        super(id, name, email, Role.ADMIN, department);
        this.accessLevel = 1;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public int getMaxBorrowQuota() {
        return 15;
    }

    @Override
    public int getLoanPeriodDays() {
        return 60;
    }
}
