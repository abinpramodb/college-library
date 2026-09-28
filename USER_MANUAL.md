# 📘 College Library Management System — End-User Operation Manual

> **Document Type**: Practical End-User Handbook  
> **Audience**: Library Staff / Librarians and Students / Members  
> **Version**: 2.4 (Production Release)  

---

## 📑 Table of Contents

- [1. Getting Started & System Access](#1-getting-started--system-access)
- [2. The Login Screen](#2-the-login-screen)
  - [2.1 Logging In as a Student](#21-logging-in-as-a-student)
  - [2.2 Logging In as a Librarian / Administrator](#22-logging-in-as-a-librarian--administrator)
  - [2.3 Troubleshooting Login Failures](#23-troubleshooting-login-failures)
- [3. Student Operations Guide](#3-student-operations-guide)
  - [3.1 Browsing & Searching Books](#31-browsing--searching-books)
  - [3.2 Understanding Availability & Stock Indicators](#32-understanding-availability--stock-indicators)
  - [3.3 Viewing Borrowed Books & Deadlines](#33-viewing-borrowed-books--deadlines)
  - [3.4 Requesting a Book Loan Renewal](#34-requesting-a-book-loan-renewal)
  - [3.5 Using Your Digital Library ID Card & Barcode](#35-using-your-digital-library-id-card--barcode)
  - [3.6 Checking Overdue Fines](#36-checking-overdue-fines)
- [4. Librarian Operations Guide](#4-librarian-operations-guide)
  - [4.1 Dashboard & Live Quick-Nav Stats](#41-dashboard--live-quick-nav-stats)
  - [4.2 Book Inventory Management](#42-book-inventory-management)
    - [Searching & Filtering Inventory](#searching--filtering-inventory)
    - [Adding a New Book Title](#adding-a-new-book-title)
    - [Deleting a Book](#deleting-a-book)
  - [4.3 Physical Copy Accession Management (Barcodes)](#43-physical-copy-accession-management-barcodes)
    - [Viewing Copies of a Book](#viewing-copies-of-a-book)
    - [Adding a New Physical Copy](#adding-a-new-physical-copy)
    - [Renewing an Issued Copy](#renewing-an-issued-copy)
  - [4.4 Circulation Desk Operations](#44-circulation-desk-operations)
    - [Workflow A: Issuing a Book to a Student](#workflow-a-issuing-a-book-to-a-student)
    - [Workflow B: Processing a Book Return](#workflow-b-processing-a-book-return)
    - [Workflow C: Calculating & Settling Overdue Fines](#workflow-c-calculating--settling-overdue-fines)
  - [4.5 User Directory & Management](#45-user-directory--management)
    - [Live User Search with Name Highlighting](#live-user-search-with-name-highlighting)
    - [Filtering by Role](#filtering-by-role)
    - [Adding a New Member](#adding-a-new-member)
  - [4.6 Circulation Reports & CSV Export](#46-circulation-reports--csv-export)
  - [4.7 Configuring Policies & Fine Rates](#47-configuring-policies--fine-rates)
- [5. Common Scenarios, Edge Cases & Error Alerts](#5-common-scenarios-edge-cases--error-alerts)
- [6. Quick-Reference Cheat Sheet](#6-quick-reference-cheat-sheet)

---

## 1. Getting Started & System Access

The library system runs directly inside any standard web browser (Google Chrome, Safari, Mozilla Firefox, Microsoft Edge, Brave) on desktop, laptop, tablet, or smartphone.

### How to Access the Portal:

| Access Method | Web Address (URL) | Notes |
|---|---|---|
| **Host Machine (Local)** | `http://localhost:8080` | Use this when working directly on the server machine. |
| **Local Wi-Fi / LAN** | `http://<HOST_IP>:8080` | For students/staff connected to the same campus Wi-Fi or hotspot. |
| **Cloud Deployment** | `https://<your-app>.onrender.com` | Permanent 24/7 online address accessible from any network. |

> **Recommended Display**: For desktop workstations, use 1280x800 resolution or higher. The UI automatically scales down smoothly for mobile phones.

---

## 2. The Login Screen

The system features a **single, unified login screen**. There are no separate login pages or dropdowns to pick your role — the system automatically identifies who you are from your ID.

```
┌────────────────────────────────────────────────────────┐
│           🏛️ COLLEGE LIBRARY MANAGEMENT SYSTEM          │
│                                                        │
│   Username / Library ID: [ 2026-CS-001               ] │
│   Password:              [ •••••••••••••             ] │
│                                                        │
│                    [  Sign In  ]                       │
└────────────────────────────────────────────────────────┘
```

### 2.1 Logging In as a Student

1. Click into the **Username / Library ID** field.
2. Enter your student matriculation ID (e.g., `2026-CS-001`, `2026-EC-014`, `2026-CE-045`).
3. Enter your account password (default: `password123`).
4. Click **Sign In** (or press `Enter`).
5. **Expected Result**: You are taken directly to the **Student Portal** (Catalogue & My Books).

---

### 2.2 Logging In as a Librarian / Administrator

1. Click into the **Username / Library ID** field.
2. Enter your staff ID: `LIB-001` or `LIB-102` (or alias `LIBRARIAN`).
3. Enter your password (default: `password123`).
4. Click **Sign In** (or press `Enter`).
5. **Expected Result**: You are taken to the **Librarian Console** with full administrative, inventory, and circulation controls.

---

### 2.3 Troubleshooting Login Failures

| Problem | Cause | How to Fix |
|---|---|---|
| *"Invalid credentials"* error popup | Mistyped ID or password | Check Caps Lock; ensure there are no trailing spaces. Default password is `password123`. |
| Page does not load / blank screen | Server process is stopped | Verify the Java backend is active (`java -jar LibraryManagementSystem.jar` or check your cloud URL). |

---

## 3. Student Operations Guide

As a student or faculty member, your portal gives you full self-service access to search books, track your active loans, and present your digital ID card.

```
┌──────────────────────────────────────────────────────────────────────────┐
│ 📚 Catalogue  |  📖 My Books (1)  |  🪪 Profile & ID  |  [ Sign Out ]    │
└──────────────────────────────────────────────────────────────────────────┘
```

---

### 3.1 Browsing & Searching Books

1. Click the **Catalogue** tab in the top navigation bar.
2. Click into the **Search** input box at the top of the page.
3. Type any search term:
   - **Book Title**: e.g., `Clean Code`, `Algorithms`, `Database`
   - **Author Name**: e.g., `Robert Martin`, `Cormen`, `Tanenbaum`
   - **Department / Category**: e.g., `Computer Science`, `Mechanical`
   - **ISBN**: e.g., `978-0132350884`
4. The book list filters **instantaneously as you type** (no need to click a submit button).
5. To clear your search, delete the text or click the **✕** button.

---

### 3.2 Understanding Availability & Stock Indicators

Every book card in the catalogue displays real-time copy stock:

```
┌────────────────────────────────────────────────────────┐
│  Clean Code: A Handbook of Agile Software Craftsmanship│
│  Robert C. Martin · Computer Science · Shelf CS-04     │
│  ────────────────────────────────────────────────────  │
│  [████████████████████░░░░░░░░░░] 3 of 4 Copies Free   │
│  ✓ IN STOCK                                            │
└────────────────────────────────────────────────────────┘
```

- **Stock Progress Bar**:
  - **Green Portion**: Available copies sitting on the library shelf.
  - **Navy Portion**: Copies currently issued out to other members.
- **Badge Indicators**:
  - `✓ IN STOCK` (Green badge): At least 1 physical copy is available for immediate checkout at the desk.
  - `⚠️ ALL ISSUED` (Amber/Red badge): All copies are currently checked out. Look at the return dates or ask the librarian to place a reservation hold.

---

### 3.3 Viewing Borrowed Books & Deadlines

1. Click the **"My Books"** tab in the top bar.
2. You will see a list of all books currently checked out to your account.
3. For each book, review:
   - **Book Title & Author**
   - **Accession Barcode** (e.g., `LIB-001-002`)
   - **Issue Date**: The date you borrowed the book.
   - **Due Date**: The deadline to return the book.
   - **Status Tag**:
     - `ACTIVE`: Loan is healthy and within the allowed borrowing term.
     - `OVERDUE`: Past due date; fine is accruing daily.

---

### 3.4 Requesting a Book Loan Renewal

Students can renew loans to extend the return deadline by an additional 14 days, provided:
- The book is not already overdue.
- The book is not reserved by another student.

**Steps to Renew**:
1. Go to **My Books**.
2. Locate the book you wish to keep longer.
3. Click the **"🔄 Renew"** button next to the loan record.
4. The system will immediately update your **Due Date** and confirm: *"Loan successfully extended by 14 days"*.

---

### 3.5 Using Your Digital Library ID Card & Barcode

You don't need a plastic ID card to borrow books. The system provides a digital card on your screen:

1. Click the **Profile** tab in the top navigation.
2. Your **Digital Library ID Card** will display:
   - Your Full Name & Photo Avatar
   - Student ID & Department
   - Membership Validity Status (`ACTIVE`)
   - **Scannable Barcode**: A high-resolution linear barcode representing your ID number.
3. **At the Circulation Desk**: Present this screen on your smartphone. The librarian can scan the barcode directly from your phone screen with a handheld barcode scanner.

---

### 3.6 Checking Overdue Fines

1. Open your **Profile** or **My Books** tab.
2. If you have overdue items, the top banner will show:
   - `⚠️ Outstanding Fines: ₹X.00`
3. To clear fines: Visit the circulation desk and pay via Cash or UPI when returning the book.

---

## 4. Librarian Operations Guide

The **Librarian Console** is the master administrative environment for running all library operations.

---

### 4.1 Dashboard & Live Quick-Nav Stats

At the top of the Librarian Console, 4 interactive stat cards display real-time numbers:

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  📖 TITLES   │  │  📚 COPIES   │  │  🎓 STUDENTS │  │  🏛️ STAFF    │
│     14       │  │     38       │  │     120      │  │      2       │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

- **Interactive Shortcut Actions**:
  - Click on **Titles** or **Copies** ➔ Instantly switches to the **Inventory** tab.
  - Click on **Students** or **Staff** ➔ Instantly switches to the **Users** tab.
- All numbers refresh dynamically whenever books or users are added or removed.

---

### 4.2 Book Inventory Management

Click the **Inventory** tab to manage titles.

#### Searching & Filtering Inventory:
1. **Search Bar**: Type any text to match title, author, ISBN, shelf location, or category.
2. **Filter Pills**: Click any pill to filter the grid in 1 click:
   - **All Books**: Clears active category filters.
   - **In Stock Only**: Filters out books with zero available copies.
   - **Issued / Checked Out**: Shows only books where copies are currently out on loan.
   - **Department Pills** (*Computer Science*, *Electronics*, *Mechanical*): Shows department-specific books.

#### Adding a New Book Title:
1. Click the green **"➕ Add New Book"** button in the top right.
2. Complete the modal form fields:
   - **Title**: e.g., *Operating System Concepts*
   - **Author**: e.g., *Silberschatz, Galvin, Gagne*
   - **ISBN**: e.g., *978-1118063330*
   - **Category / Department**: Select or type department (e.g. `Computer Science`).
   - **Shelf Code**: Physical library rack location (e.g. `CS-07-B`).
   - **Initial Copies**: Number of physical books to accession immediately (e.g. `3`).
3. Click **"Save & Accession"**.
4. The new title card appears in the catalogue, and individual copy barcodes are created automatically.

#### Deleting a Book:
1. Locate the book card in the inventory list.
2. Click the red **"🗑️ Delete"** button on the bottom of the card.
3. Confirm the prompt: *"Are you sure you want to remove this book title?"*.
4. **Note**: A book cannot be deleted if any of its copies are currently issued to students. All copies must be returned first.

---

### 4.3 Physical Copy Accession Management (Barcodes)

Each physical book on the shelf has its own individual accession copy record and barcode.

#### Viewing Copies of a Book:
1. On any book card, click **"View Copies →"**.
2. The **Physical Copy Accession Modal** opens, showing a table of all physical books:

```
┌────────────────────────────────────────────────────────────────────────┐
│ 📚 Physical Copies: Introduction to Algorithms                         │
├──────────────┬──────────┬───────────┬──────────────┬──────────────────┤
│ Barcode      │ Shelf    │ Status    │ Borrower     │ Actions          │
├──────────────┼──────────┼───────────┼──────────────┼──────────────────┤
│ LIB-002-001  │ CS-02    │ AVAILABLE │ —            │ [Mark Lost]      │
│ LIB-002-002  │ CS-02    │ ISSUED    │ 2026-CS-001  │ [🔄 Renew Loan]  │
│ LIB-002-003  │ CS-02    │ AVAILABLE │ —            │ [Mark Lost]      │
└──────────────┴──────────┴───────────┴──────────────┴──────────────────┘
│ [ ➕ Add Copy ]                                      [ Close Modal ]  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Adding a New Physical Copy:
1. Inside the modal, click **"➕ Add Copy"**.
2. The system automatically calculates the next copy index and assigns a standard barcode:
   `LIB-{BookID}-{CopyIndex}` (e.g. `LIB-002-004`).
3. The new copy is immediately marked as `AVAILABLE` and ready for circulation.

#### Renewing an Issued Copy:
1. Inside the copy modal, find any row marked `ISSUED`.
2. Click the **"🔄 Renew Loan"** button next to that copy.
3. The loan term is extended by another 14 days, and the new due date is displayed.

---

### 4.4 Circulation Desk Operations

Click the **Circulation** tab to perform book checkouts, returns, and fine processing.

#### Workflow A: Issuing a Book to a Student

```
[ Step 1: Scan Student ID ] ──> [ Step 2: Scan Copy Barcode ] ──> [ Step 3: Click Issue ]
```

1. Click the **"Issue Book"** tab at the circulation desk.
2. **Student ID Field**:
   - Type the student ID (e.g. `2026-CS-001`) or scan the student's digital card barcode.
3. **Copy Barcode Field**:
   - Type the copy barcode (e.g. `LIB-001-002`) or scan the physical book's barcode sticker.
4. Click **"Complete Checkout"** (or press `Enter`).
5. **System Validations**:
   - Checks if the user exists and is active.
   - Checks if the student has reached their **max borrow quota** (limit: 3 books).
   - Checks if the student has **unpaid overdue fines**.
   - Checks if the specific copy is **available**.
6. **Result**:
   - If validations pass: Success banner displays due date (14 days from today), and the copy status switches to `ISSUED`.
   - If validation fails: A clear alert explains the reason (e.g., *"Student has already borrowed 3 books. Limit reached."*).

---

#### Workflow B: Processing a Book Return

1. Click the **"Return Book"** tab at the circulation desk.
2. Enter or scan the **Copy Barcode** of the returned book (e.g. `LIB-001-002`).
3. Click **"Process Return"**.
4. **On-Time Return**:
   - If returned on or before the due date: The book is immediately marked `AVAILABLE`, the loan record is closed, and a green success confirmation is shown.
5. **Overdue Return**:
   - If returned past the due date: The system calculates the overdue fine automatically (see Workflow C below).

---

#### Workflow C: Calculating & Settling Overdue Fines

When an overdue book is returned, the **Fine Settlement Modal** opens automatically:

```
┌────────────────────────────────────────────────────────┐
│ ⚠️ Overdue Book Return Detected                         │
│                                                        │
│ Borrower:       Rahul Sharma (2026-CS-001)             │
│ Book:           Clean Code (LIB-001-002)               │
│ Days Overdue:   4 Days                                 │
│ Fine Rate:      ₹2.00 / day                            │
│ Total Penalty:  ₹8.00                                  │
│                                                        │
│ Payment Method:                                        │
│  ( ) Cash       (•) UPI / QR       ( ) Waived (Admin)  │
│                                                        │
│              [ Confirm Payment & Return ]              │
└────────────────────────────────────────────────────────┘
```

1. Review the calculated fine amount.
2. Select the payment method used by the student:
   - **Cash**: Settle immediately at the cash drawer.
   - **UPI / QR**: Student scans the counter UPI QR code with their mobile banking/payment app.
   - **Waived**: Authorized administrative waiver (requires librarian confirmation).
3. Click **"Confirm Payment & Return"**.
4. The system logs the financial transaction in the audit ledger, clears the fine from the student's account, and returns the book copy to `AVAILABLE` status.

---

### 4.5 User Directory & Management

Click the **Users** tab to view and manage registered members.

#### Live User Search with Name Highlighting:
1. Type any name, student ID, department, or email in the top search box.
2. The user table filters instantly.
3. The exact matching letters in the user's name are **highlighted in bold gold/yellow**:
   - *Example*: Typing `kumar` highlights "**Kumar**" inside `Aditya Kumar` and `Suresh Kumar`.
4. Click the **✕** button to clear the search filter and view all members again.

#### Filtering by Role:
- Click the role filter pills: **All Members**, **Students Only**, **Faculty Only**, or **Librarians Only**.

#### Adding a New Member:
1. Click **"➕ Register Member"** at the top right of the Users tab.
2. Fill in: Full Name, Role (Student / Faculty / Librarian), Department, Email, and assign a unique Library ID.
3. Click **"Create Member"**.

---

### 4.6 Circulation Reports & CSV Export

Click the **Reports** tab to review all circulation records.

1. **Transaction Table**: Shows historical and live records including Transaction ID, Copy Barcode, Borrower ID, Issue Date, Due Date, Return Date, and Fine Paid.
2. **Exporting to Spreadsheet (CSV)**:
   - Click the blue **"📥 Export CSV"** button at the top right.
   - The browser automatically downloads a file: `library_circulation_report.csv`.
   - Open this file in Microsoft Excel, Google Sheets, or LibreOffice Calc for audit inspections, accreditation documentation, or administrative reporting.

---

### 4.7 Configuring Policies & Fine Rates

Click the **Rules** tab to adjust operational parameters without editing code:

| Setting | Default Value | Description |
|---|---|---|
| **Daily Fine Rate** | `₹2.00` | Overdue charge accrued per book per day past the due date. |
| **Student Borrow Quota** | `3 Books` | Maximum concurrent active loans allowed per student account. |
| **Faculty Borrow Quota** | `5 Books` | Maximum concurrent active loans allowed per faculty account. |
| **Student Loan Period** | `14 Days` | Standard checkout duration for students before a book is overdue. |
| **Faculty Loan Period** | `30 Days` | Standard checkout duration for faculty members. |

- Edit any number and click **"Save Policy Changes"** to apply immediately across the entire system.

---

## 5. Common Scenarios, Edge Cases & Error Alerts

### Scenario 1: Student reaches borrow quota limit
- **Situation**: Student tries to check out a 4th book when quota is 3.
- **System Action**: Checkout is blocked. Alert: *"Quota Exceeded: User 2026-CS-001 already has 3 books checked out. Return an existing book first."*
- **Resolution**: Student must return an existing active loan before borrowing a new book.

### Scenario 2: Student has unpaid overdue fines
- **Situation**: Student with an outstanding ₹10 fine attempts to borrow a book.
- **System Action**: Checkout is paused. Alert: *"Outstanding fine exists on this account. Settle ₹10.00 fine before issuing new books."*
- **Resolution**: Go to Circulation desk, settle the fine, and proceed with the loan.

### Scenario 3: Physical copy is damaged or lost
- **Situation**: A student reports a lost book.
- **Resolution**:
  1. Open the **Book Inventory** tab.
  2. Click **View Copies →** on the book card.
  3. Locate the copy barcode and change status to `DAMAGED` or `LOST`.
  4. The copy is removed from active circulation, and the title's available stock count drops automatically.

### Scenario 4: Multiple students want the same book
- **Situation**: All copies of a high-demand book (e.g. *Clean Code*) are currently issued.
- **Resolution**:
  - The book card automatically displays `⚠️ ALL ISSUED`.
  - Librarians can click **➕ Add Copy** in the copies modal to accession additional shelf copies.

---

## 6. Quick-Reference Cheat Sheet

### Default Login Accounts

| Role | Username / ID | Password | Access Level |
|---|---|---|---|
| **Senior Librarian** | `LIB-001` | `password123` | Full administrative console, inventory, rules, reports |
| **Assistant Librarian** | `LIB-102` | `password123` | Full circulation, member directory & catalog access |
| **Computer Science Student** | `2026-CS-001` | `password123` | Student portal, catalogue search, active loans, digital card |
| **Electronics Student** | `2026-EC-014` | `password123` | Student portal, catalogue search, active loans, digital card |
| **Mechanical Student** | `2026-ME-022` | `password123` | Student portal, catalogue search, active loans, digital card |
| **Civil Student** | `2026-CE-045` | `password123` | Student portal, catalogue search, active loans, digital card |
| **Faculty Member** | `FAC-102` | `password123` | Extended borrowing quota (5 books) & 30-day loans |

### Barcode Formats
- **Physical Book Copies**: `LIB-{BookID}-{CopyIndex}` (e.g., `LIB-001-001`, `LIB-001-002`)
- **Student ID Barcodes**: `2026-{DEPT}-{ROLL}` (e.g., `2026-CS-001`)

---

*College Library Management System — End-User Operation Manual*
