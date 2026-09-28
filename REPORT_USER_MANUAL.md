# CHAPTER: USER MANUAL & OPERATING INSTRUCTIONS

---

## 1. System Requirements & Launch Instructions

### 1.1 Minimum System Requirements
- **Operating System**: Windows 10/11, macOS 12+, or Ubuntu Linux 20.04+
- **Runtime Environment**: Java Development Kit (JDK) 17 or 21 LTS
- **Web Browser**: Any modern browser (Google Chrome, Mozilla Firefox, Microsoft Edge, Safari)
- **Memory (RAM)**: Minimum 2 GB RAM (4 GB recommended)
- **Storage**: 100 MB free disk space

### 1.2 How to Start the Application
1. Open the terminal or command prompt in the project root directory.
2. Execute the standalone executable JAR file:
   ```bash
   java -jar LibraryManagementSystem.jar
   ```
3. Open any web browser and navigate to:
   ```text
   http://localhost:8080
   ```
   *(For remote access or deployment, the application can also be accessed via its hosted cloud URL).*

---

## 2. Authentication & User Roles

The system uses a **Single Unified Login Screen**. Users do not need to manually select a role — the system automatically identifies the user type based on the entered ID format:

```
┌────────────────────────────────────────────────────────┐
│           COLLEGE LIBRARY MANAGEMENT SYSTEM            │
│                                                        │
│   Library ID / Username : [ 2026-CS-001              ] │
│   Password              : [ •••••••••••••            ] │
│                                                        │
│                    [  Sign In  ]                       │
└────────────────────────────────────────────────────────┘
```

### Default Login Credentials for Demonstration:

| Role | User ID / Username | Password | Permitted Operations |
| :--- | :--- | :--- | :--- |
| **Senior Librarian** | `LIB-001` | `password123` | Full administrative, inventory, policy & circulation controls |
| **Assistant Librarian**| `LIB-102` | `password123` | Circulation counter, book cataloguing & member directory |
| **Student (CS)** | `2026-CS-001` | `password123` | Catalogue search, active loan tracking & digital ID card |
| **Student (EC)** | `2026-EC-014` | `password123` | Catalogue search, active loan tracking & digital ID card |
| **Faculty Member** | `FAC-102` | `password123` | Extended borrowing quota (5 books) & 30-day loan duration |

---

## 3. Student Portal Walkthrough

Once authenticated with a Student ID, the system presents the self-service student interface:

### 3.1 Searching and Browsing Books (Catalogue Tab)
1. Navigate to the **Catalogue** tab.
2. Type a keyword (book title, author name, ISBN, or subject) into the live search bar.
3. The catalog filters instantaneously without page reloads.
4. **Stock Indicators**:
   - `✓ IN STOCK` (Green): At least one physical copy is available on the library shelf.
   - `⚠️ ALL ISSUED` (Amber/Red): All copies are currently checked out.

### 3.2 Managing Active Loans & Renewals (My Books Tab)
1. Click **My Books** to view currently borrowed items.
2. Review the **Issue Date**, **Due Date**, and **Remaining Days** for each book.
3. **Renewing a Loan**:
   - Click the **"🔄 Renew"** button next to an eligible book.
   - If the book is not overdue and has no reservations, the due date is automatically extended by an additional 14 days.

### 3.3 Digital Library ID Card (Profile Tab)
1. Click the **Profile** tab.
2. Displays the student's name, matriculation ID, branch, and status.
3. Features a **dynamic scannable linear barcode** that can be presented at the circulation counter directly from a smartphone screen.

---

## 4. Librarian Portal Walkthrough

Upon logging in with a Librarian ID (`LIB-001`), the administrative console opens with full circulation and cataloguing powers.

### 4.1 Dashboard Overview
- Displays 4 live statistical counters: **Total Book Titles**, **Physical Copies**, **Registered Students**, and **Staff**.
- **Interactive Navigation**: Clicking on the stat cards navigates directly to the corresponding inventory or user management module.

### 4.2 Book Inventory Management (Inventory Tab)
- **Live Filtering**: Search by title, author, shelf location, or use one-click category pills (*In Stock*, *Issued*, *Computer Science*, *Electronics*).
- **Adding a New Book Title**:
  1. Click **"➕ Add New Book"**.
  2. Enter Title, Author, ISBN, Category, Shelf Location, and Initial Copies.
  3. Click **Save Book**.
- **Physical Copy Accession Modal**:
  1. Click **"View Copies →"** on any book card.
  2. Displays each individual copy with its accession barcode (e.g. `LIB-001-001`, `LIB-001-002`).
  3. Click **"➕ Add Copy"** to accession new physical copies with auto-generated barcodes.

---

## 5. Circulation Desk Operations

### 5.1 Issuing a Book (Checkout)
1. Navigate to the **Circulation** tab and select **Issue Book**.
2. Enter or scan the **Student ID** (e.g. `2026-CS-001`).
3. Enter or scan the **Copy Barcode** (e.g. `LIB-001-002`).
4. Click **Complete Checkout**.
5. **System Validation**:
   - Verifies the student's borrowing limit (maximum 3 books for students).
   - Verifies there are no unpaid overdue penalties.
   - Verifies the selected copy is currently marked as `AVAILABLE`.
6. Upon approval, the copy transitions to `ISSUED`, and a 14-day loan record is registered.

### 5.2 Returning a Book & Fine Settlement
1. Select **Return Book** at the circulation counter.
2. Enter or scan the **Copy Barcode**.
3. Click **Process Return**.
4. **Automated Fine Handling**:
   - If returned within the 14-day limit: Book is restored to `AVAILABLE` immediately with ₹0 fine.
   - If returned past the due date: The system computes the fine based on overdue days (e.g., ₹2.00/day).
   - The **Fine Settlement Dialog** prompts the librarian to collect payment via **Cash**, **UPI QR**, or record an administrative **Waiver**.
   - Once cleared, the fine ledger is updated, and the copy returns to active shelf inventory.

---

## 6. Reports, Audit & System Policy

### 6.1 Circulation Reports & CSV Export
1. Click the **Reports** tab to inspect the complete history of all issues, returns, and renewals.
2. Click **"📥 Export CSV"** to download the official circulation report spreadsheet for accreditation or departmental record-keeping.

### 6.2 Policy & Fine Rule Configuration (Rules Tab)
Librarians can modify operational library parameters without altering source code:
- **Daily Overdue Fine Rate** (Default: ₹2.00 / day)
- **Student Borrow Limit** (Default: 3 books)
- **Loan Duration** (Default: 14 days for students, 30 days for faculty)

---

## 7. Operational Troubleshooting Summary

| Symptom / Event | System Root Cause | Recommended Action |
| :--- | :--- | :--- |
| **"Quota Exceeded" alert** | Student has already borrowed 3 books. | Student must return an existing loan before borrowing a new title. |
| **"Outstanding Fine" alert** | Overdue fine exists on student profile. | Settle outstanding balance at counter before issuing new books. |
| **"Copy Not Available"** | Selected book copy is already issued out. | Issue a different copy or check estimated return date in the inventory modal. |
| **Offline Campus Usage** | Server operates on local network. | Connect client devices to the same Wi-Fi network and access via the host IP address. |
