package com.library.services;

import com.library.models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserService {
    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    public UserService() {
        seedUsers();
    }

    public Optional<User> getUserById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usersById.get(id.trim().toUpperCase()));
    }

    public Optional<Student> getStudentById(String id) {
        return getUserById(id)
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    public List<Student> getAllStudents() {
        return usersById.values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) return getAllUsers();
        return usersById.values().stream()
                .filter(u -> u.matches(query))
                .collect(Collectors.toList());
    }

    public synchronized void registerUser(User user) {
        usersById.put(user.getId().toUpperCase(), user);
    }

    public synchronized void updateOrRegisterUser(String origId, String newId, String name, String password, String dept, String roleStr, String status) {
        if (origId != null && !origId.trim().equalsIgnoreCase(newId.trim())) {
            usersById.remove(origId.trim().toUpperCase());
        }
        Optional<User> existing = getUserById(newId);
        User u;
        if (existing.isPresent()) {
            u = existing.get();
            u.setName(name);
            u.setId(newId);
            u.setDepartment(dept);
            if (password != null && !password.isBlank()) {
                u.setPassword(password);
            }
            u.setActive(!"inactive".equalsIgnoreCase(status) && !"suspended".equalsIgnoreCase(status));
        } else {
            String email = newId.toLowerCase() + "@college.edu";
            if ("Librarian".equalsIgnoreCase(roleStr) || "Admin".equalsIgnoreCase(roleStr)) {
                u = new Librarian(newId, name, email, dept, "Circulation Desk");
            } else if ("Faculty".equalsIgnoreCase(roleStr)) {
                u = new Faculty(newId, name, email, dept, "Faculty Member");
            } else {
                u = new Student(newId, name, email, dept, "Semester");
            }
            if (password != null && !password.isBlank()) {
                u.setPassword(password);
            }
            u.setActive(!"inactive".equalsIgnoreCase(status) && !"suspended".equalsIgnoreCase(status));
            registerUser(u);
        }
    }

    public synchronized boolean deleteUser(String id) {
        if (id == null) return false;
        return usersById.remove(id.trim().toUpperCase()) != null;
    }

    private void seedUsers() {
        // Students
        Student s0 = new Student("2026CE045", "Student", "student@college.edu", "Computer Engineering", "6th Sem");
        s0.addFine(40.0);
        Student s1 = new Student("2026CS142", "Student", "student.cs@college.edu", "Computer Science", "6th Sem");
        s1.addFine(40.0); // Has ₹40 fine for Database Systems

        Student s2 = new Student("2026CS108", "Rahul Verma", "rahul.v@college.edu", "Computer Science", "6th Sem");
        Student s3 = new Student("2026EC045", "Ananya Iyer", "ananya.i@college.edu", "Electronics & Comm", "4th Sem");
        Student s4 = new Student("2026ME089", "Aditya Nair", "aditya.n@college.edu", "Mechanical Eng", "4th Sem");

        registerUser(s0);
        registerUser(s1);
        registerUser(s2);
        registerUser(s3);
        registerUser(s4);

        // Librarians
        Librarian l1 = new Librarian("LIB-001", "Librarian", "librarian@college.edu", "Library Services", "Circulation Desk 1");
        registerUser(l1);
        Librarian l2 = new Librarian("LIB-102", "Suresh Kumar", "suresh.k@college.edu", "Library Services", "Circulation Desk #102");
        registerUser(l2);

        // Faculty
        Faculty f1 = new Faculty("FAC-102", "Dr. K. Ramanathan", "ramanathan.k@college.edu", "Computer Science", "Professor");
        registerUser(f1);

        // Staff compatibility alias
        Librarian a1 = new Librarian("ADM-001", "Librarian", "librarian@college.edu", "Library Services", "Central Library");
        registerUser(a1);
    }
}
