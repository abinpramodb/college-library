# CHAPTER: WEBSITE USER MANUAL

> **Live Website URL**: [https://college-library-zqd2.onrender.com/](https://college-library-zqd2.onrender.com/)  
> **Deployment Platform**: Cloud-Hosted on Render (24/7 Global Availability)  
> **System Nature**: 100% Online Web Application (Zero local software / No localhost)

---

## 1. Introduction

The **College Library Management System** is a live public cloud website hosted online at **`https://college-library-zqd2.onrender.com/`**. 

It runs on cloud servers 24 hours a day, 7 days a week. Users (students, faculty, and library staff) do not run anything locally on their computers — they simply open the website URL in any browser on any phone, tablet, or laptop.

---

## 2. How to Access the Website

1. Open your web browser (Chrome, Safari, Firefox, Edge, etc.) on your mobile phone, laptop, or computer.
2. Go to the website URL:
   ```text
   https://college-library-zqd2.onrender.com/
   ```
3. The website loads immediately with secure HTTPS encryption.

---

## 3. Website Login

The website features a single login box for all users. The system automatically detects whether you are a Student or a Librarian based on your ID.

```
┌────────────────────────────────────────────────────────┐
│           🏛️ COLLEGE LIBRARY WEB PORTAL                │
│                                                        │
│   Library ID / Username : [ 2026-CS-001              ] │
│   Password              : [ •••••••••••••            ] │
│                                                        │
│                    [  Sign In  ]                       │
└────────────────────────────────────────────────────────┘
```

### Test Accounts:

| User Type | Username / ID | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **Student** | `2026-CS-001` | `password123` | Search books, view active loans, renew books, digital ID card |
| **Student** | `2026-EC-014` | `password123` | Search books, view active loans, renew books, digital ID card |
| **Faculty** | `FAC-102` | `password123` | Extended borrowing quota (5 books) & 30-day loan duration |
| **Librarian** | `LIB-001` | `password123` | Full access to add/delete books, issue/return, and manage users |

---

## 4. Student User Guide

When you log in with a Student ID, the website displays the **Student Portal**:

### 4.1 Searching for Books Online
- Click the **Catalogue** tab.
- Type any book title, author name, category, or ISBN into the search bar.
- The website filters the book list instantly as you type.
- **Stock Status**:
  - `✓ IN STOCK`: Copies are available in the library.
  - `⚠️ ALL ISSUED`: All copies are currently borrowed.

### 4.2 Viewing Your Borrowed Books & Due Dates
- Click the **My Books** tab.
- You will see the list of all books you have currently borrowed, along with their issue dates and return due dates.

### 4.3 Renewing a Book Online
- Under **My Books**, click the **"🔄 Renew"** button next to any eligible book.
- The website automatically extends your return due date by 14 days.

### 4.4 Your Digital Library ID Card
- Click the **Profile** tab.
- The website displays your digital library card with your name, student ID, and a scannable barcode for quick identification.

---

## 5. Librarian User Guide

When you log in with a Librarian ID (`LIB-001`), the website opens the **Librarian Console**:

### 5.1 Dashboard Overview
- Live cards show total **Book Titles**, **Physical Copies**, **Students**, and **Librarians**.
- Clicking any card takes you directly to that section of the website.

### 5.2 Managing Book Inventory
- Click the **Inventory** tab.
- **Search & Filters**: Search by title or author, or use the quick filter pills (*In Stock*, *Issued*, *Computer Science*, *Electronics*, *Mechanical*).
- **Add a Book**: Click **"➕ Add New Book"**, enter the book details (Title, Author, ISBN, Category, Shelf Code), and click **Save**.
- **Delete a Book**: Click the **"🗑️ Delete"** button on any book card to remove it.
- **Manage Physical Copies**: Click **"View Copies →"** on any book card to see all copy barcodes, or click **"➕ Add Copy"** to add new physical copies.

### 5.3 Issuing & Returning Books Online (Circulation Desk)
- Click the **Circulation** tab:
  - **To Issue a Book**: Enter the Student ID and Book Copy Barcode, then click **Complete Checkout**.
  - **To Return a Book**: Enter the Book Copy Barcode and click **Process Return**.
  - **Overdue Fines**: If a book is returned late, the website automatically calculates the fine (₹2.00 per day) and records the payment (Cash, UPI, or Waived).

### 5.4 Managing Users
- Click the **Users** tab to view all registered students and staff.
- Search any member by name, ID, or department with real-time text highlighting.
- Click **"➕ Register Member"** to create a new student or librarian account.

### 5.5 Downloading Reports
- Click the **Reports** tab to view the complete history of all book checkouts and returns.
- Click **"📥 Export CSV"** to download the official circulation report spreadsheet to your device.

---

## 6. Summary

Because the library system is deployed as a cloud website on Render, it offers:
- **Zero Installation**: Accessible instantly on any device with a browser.
- **24/7 Availability**: The server is always running online.
- **Cross-Platform**: Works identically on Android, iOS, Windows, Mac, and Linux.
