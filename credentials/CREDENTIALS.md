# 🔐 College Library Management System — User Credentials Directory

All user accounts, staff logins, and student credentials for the Library Management System.

---

## 🌐 Portal Access
- **Cloudflare Public HTTPS**: [https://annex-outlet-placing-municipality.trycloudflare.com/](https://annex-outlet-placing-municipality.trycloudflare.com/)
- **Local Application URL**: [http://localhost:8080/](http://localhost:8080/)

> **Note**: Both Student and Librarian use the **same unified login screen**. The system automatically detects your role based on your ID.

---

## 🛡️ 1. Librarian Accounts
Logging in with any of these IDs opens the **Librarian Console** (Circulation Station, Cataloging, Member Directory, Fines & Audit Logs).

| Role | Staff / User ID | Password | Name | Department / Station |
| :--- | :--- | :--- | :--- | :--- |
| **Librarian** | `LIB-001` | `password123` | Librarian | Library Services |
| **Circulation Desk** | `LIB-102` | `password123` | Suresh Kumar | Station #102 |

*Alias for quick access*: You can also log in as Librarian using ID: `LIBRARIAN` with password `password123`.

---

## 📱 2. Student Accounts
Logging in with any of these IDs opens the **Student Portal** (Catalogue Search, Book Reservations, Active Loans, Digital Library ID Card, and Fines).

| Student ID | Password | Student Name | Department & Semester | Current Active Loans |
| :--- | :--- | :--- | :--- | :--- |
| `2026CE045` | `password123` | Student Member | Computer Engineering (S6) | 3 Books (1 Overdue, ₹40 Fine) |
| `2026CS142` | `password123` | Student CS | Computer Science (S6) | 2 Books |
| `2026CS108` | `password123` | Rahul Verma | Computer Science (S6) | 1 Book |
| `2026EC045` | `password123` | Ananya Iyer | Electronics & Comm (S4) | 0 Books |
| `2026ME089` | `password123` | Aditya Nair | Mechanical Engg (S4) | 2 Books |
| `2026ME031` | `password123` | Riya Sharma | Mechanical Engg (S6) | 2 Books |
| `2025CS112` | `password123` | Dev Nair | Computer Science (S7) | 4 Books (Limit reached) |

---

## 🎓 3. Faculty Accounts

| Faculty ID | Password | Faculty Name | Department / Designation | Book Limit |
| :--- | :--- | :--- | :--- | :--- |
| `FAC-102` | `password123` | Dr. K. Ramanathan | Computer Science (Professor) | 10 Books (60 Days) |
| `FAC-045` | `password123` | Dr. Anita Roy | Computer Science (Associate Prof) | 10 Books (60 Days) |

---

## 💡 Quick Tips
1. Any password will authenticate in offline local fallback mode, but `password123` is the standard project password.
2. The user session stays saved on the device if **Remember me** is checked.
3. Click the **🚪 Sign Out** button in the top right header at any time to return to the login screen.
