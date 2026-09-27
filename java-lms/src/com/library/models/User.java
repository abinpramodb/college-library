package com.library.models;

import com.library.enums.Role;
import com.library.interfaces.Searchable;

public abstract class User implements Searchable {
    protected String id;
    protected String name;
    protected String email;
    protected Role role;
    protected String department;
    protected boolean active;
    protected String password = "password123";

    public User(String id, String name, String email, Role role, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.active = true;
        this.password = "password123";
    }

    public String getPassword() {
        return password != null ? password : "password123";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract int getMaxBorrowQuota();
    public abstract int getLoanPeriodDays();

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) return true;
        String q = query.toLowerCase().trim();
        return id.toLowerCase().contains(q) ||
               name.toLowerCase().contains(q) ||
               (email != null && email.toLowerCase().contains(q)) ||
               (department != null && department.toLowerCase().contains(q));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Dept: %s", role.getDisplayName(), name, id, department);
    }
}
