# CHAPTER: WEB APPLICATION USER MANUAL & OPERATING GUIDE

---

## 1. System Overview & Cloud Access

The **College Library Management System** is a 24/7 cloud-hosted web application deployed on **Render**. It requires **zero installation** on client devices and can be accessed from any smartphone, laptop, tablet, or desktop computer with an internet browser.

### 1.1 How to Access the Website
1. Open any web browser (Google Chrome, Safari, Mozilla Firefox, Microsoft Edge, Brave).
2. Enter the live hosted web address:
   ```text
   https://college-library-system.onrender.com
   ```
3. The home page loads instantly with full secure **HTTPS / SSL encryption**.

### 1.2 Client Device Requirements
- **Hardware**: Any internet-enabled smartphone, tablet, laptop, or desktop computer.
- **Software**: Any standard modern web browser.
- **Client Prerequisites**: None. No Java runtime, plugins, or software downloads are required on the user's device.

---

## 2. Authentication & Unified Web Login

The website features a **Single Unified Login Screen**. Users do not need to manually choose whether they are a Student or a Librarian — the system automatically identifies the role from the entered Library ID:

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

### Demonstration Accounts & Credentials:

| Role | User ID / Username | Password | Web Portal Features |
| :--- | :--- | :--- | :--- |
| **Senior Librarian** | `LIB-001` | `password123` | Full administrative console, inventory, circulation & reports |
| **Assistant Librarian**| `LIB-102` | `password123` | Circulation desk, book cataloguing & member search |
| **Student (CS)** | `2026-CS-001` | `password123` | Online catalogue, active loans, renewals & digital card |
| **Student (EC)** | `2026-EC-014` | `password123` | Online catalogue, active loans, renewals & digital card |
| **Faculty Member** | `FAC-102` | `password123` | Extended borrowing quota (5 books) & 30-day loan duration |

---

## 3. Student Web Portal Walkthrough

When logging in with a Student ID, the web portal provides a self-service interface for all student library needs:

### 3.1 Online Book Search (Catalogue Tab)
1. Click the **Catalogue** tab in the navigation bar.
2. Enter any keyword into the live search bar (Book Title, Author, ISBN, or Subject).
3. The catalog filters in real time without refreshing the page.
4. **Visual Stock Indicators**:
   - `✓ IN STOCK` (Green badge): Physical copies are available on the library shelf.
   - `⚠️ ALL ISSUED` (Amber/Red badge): All copies are currently checked out by other members.
   - **Stock Ratio Bar**: Visual green and navy bar showing the exact proportion of available vs. issued copies.

### 3.2 Managing Active Loans & Renewals (My Books Tab)
1. Click **My Books** to view your active book checkouts.
2. Check the **Issue Date**, **Due Date**, and **Days Remaining** for each borrowed book.
3. **1-Click Loan Renewal**:
   - Click the **"🔄 Renew"** button next to an eligible loan.
   - If the book is not overdue and has no reservations, the due date is automatically extended by an additional 14 days.

### 3.3 Digital Library ID Card & Scannable Barcode (Profile Tab)
1. Click the **Profile** tab.
2. The page generates your **Official Digital Library ID Card**:
   - Displays Student Name, Roll Number, Branch, and Validity.
   - Renders a **high-resolution scannable barcode**.
3. **At the Physical Counter**: Present your phone screen to the librarian; the barcode can be scanned directly from your screen for rapid book checkout.

---

## 4. Librarian Administrative Console Walkthrough

Logging in with a Librarian ID (`LIB-001`) opens the full administrative management dashboard.

### 4.1 Dashboard & Live Quick-Navigation
- Displays 4 live statistical counters: **Book Titles**, **Physical Copies**, **Registered Students**, and **Staff**.
- **1-Click Navigation**: Clicking on the stat cards immediately navigates to the respective inventory or member management module.

### 4.2 Book Inventory Management (Inventory Tab)
- **Live Search & Category Pills**: Filter books instantly using one-click filter pills (*All Books*, *In Stock Only*, *Issued*, *Computer Science*, *Electronics*, *Mechanical*).
- **Adding a New Book Title**:
  1. Click the green **"➕ Add New Book"** button.
  2. Fill in Title, Author, ISBN, Category, Shelf Code, and Initial Copies.
  3. Click **Save Book** to register the title in the live database.
- **Physical Copy Accession Modal**:
  1. Click **"View Copies →"** on any book card.
  2. View individual copies with their unique barcodes (e.g. `LIB-001-001`, `LIB-001-002`) and shelf locations.
  3. Click **"➕ Add Copy"** to accession new physical copies with auto-generated barcodes.

---

## 5. Web Circulation Desk Operations

### 5.1 Issuing a Book to a Student
1. Open the **Circulation** tab and select **Issue Book**.
2. Enter or scan the **Student ID** (e.g. `2026-CS-001`).
3. Enter or scan the **Copy Barcode** (e.g. `LIB-001-002`).
4. Click **Complete Checkout**.
5. **Automated Validation Rules**:
   - Checks that the student has not exceeded the 3-book borrow limit.
   - Checks that the student has no unpaid overdue fines.
   - Verifies the requested copy is `AVAILABLE`.
6. Upon approval, the status updates to `ISSUED`, and a 14-day checkout deadline is set.

### 5.2 Returning a Book & Automated Fine Settlement
1. Select **Return Book** at the circulation desk.
2. Enter or scan the **Copy Barcode**.
3. Click **Process Return**.
4. **Automated Overdue Calculation**:
   - If returned on time: The copy is marked `AVAILABLE` with ₹0 fine.
   - If returned late: The system automatically computes the fine based on days overdue (e.g., ₹2.00 / day).
   - The **Fine Settlement Dialog** allows the librarian to record payment via **Cash**, **UPI QR Code**, or apply an authorized **Waiver**.
   - Upon confirmation, the fine is cleared, and the book is restored to the active catalogue.

---

## 6. Reports, Audit Trail & Policy Configuration

### 6.1 Circulation Reports & CSV Export (Reports Tab)
1. View a chronological audit log of all book checkouts, returns, renewals, and fine payments.
2. Click **"📥 Export CSV"** to download an official spreadsheet report for college administration and NAAC/NBA accreditation inspections.

### 6.2 Policy & Fine Rule Configuration (Rules Tab)
Librarians can modify operational library parameters live on the website:
- **Daily Overdue Fine Rate** (Default: ₹2.00 / day)
- **Student Borrow Limit** (Default: 3 books)
- **Standard Loan Periods** (14 days for students, 30 days for faculty)

---

## 7. Web Troubleshooting & FAQs

| Scenario | System Handling | User Action |
| :--- | :--- | :--- |
| **"Quota Exceeded" message** | Student already has 3 active loans. | Return a borrowed book before checking out a new one. |
| **"Outstanding Fine" message** | Unpaid overdue penalty exists on account. | Settle fine at counter via Cash or UPI before borrowing. |
| **Book shows "ALL ISSUED"** | All physical shelf copies are currently on loan. | Check return due dates or request staff to accession more copies. |
| **Accessing on Smartphones** | Fully responsive web interface. | Open browser, visit URL, and log in directly without installing any app. |
