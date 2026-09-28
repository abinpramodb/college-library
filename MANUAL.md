# 📖 College Library Management System — User & Administrator Manual

> A complete, step-by-step guide to running, managing, and using the College Library Management System.

---

## 📑 Table of Contents

1. [System Overview](#1-system-overview)
2. [Technology Stack](#2-technology-stack)
3. [How to Launch & Access the Application](#3-how-to-launch--access-the-application)
   - [A. Running Locally on Your Computer](#a-running-locally-on-your-computer)
   - [B. Accessing Over Local Wi-Fi / Hotspot](#b-accessing-over-local-wi-fi--hotspot)
   - [C. Accessing from Anywhere via Cloud (Render)](#c-accessing-from-anywhere-via-cloud-render)
   - [D. Instant Tunnel (Cloudflare / Pinggy)](#d-instant-tunnel-cloudflare--pinggy)
4. [User Authentication & Portals](#4-user-authentication--portals)
   - [Unified Login](#unified-login)
   - [Default Accounts & Credentials](#default-accounts--credentials)
5. [Librarian / Staff Guide](#5-librarian--staff-guide)
   - [Dashboard & Live Stat Counters](#dashboard--live-stat-counters)
   - [Book Inventory & Cataloguing](#book-inventory--cataloguing)
   - [Physical Copy Accession Management](#physical-copy-accession-management)
   - [Circulation Desk (Issue, Return, Renew)](#circulation-desk-issue-return-renew)
   - [User Management & Search](#user-management--search)
   - [Reports, Logs & Analytics](#reports-logs--analytics)
   - [Library Policy & Fine Rules](#library-policy--fine-rules)
6. [Student & Member Guide](#6-student--member-guide)
   - [Browsing & Searching Books](#browsing--searching-books)
   - [Viewing Active Loans & Renewals](#viewing-active-loans--renewals)
   - [Digital Library ID Card](#digital-library-id-card)
7. [System Architecture & Design Patterns](#7-system-architecture--design-patterns)
8. [Frequently Asked Questions (FAQ)](#8-frequently-asked-questions-faq)

---

## 1. System Overview

The **College Library Management System** is a standalone, enterprise-grade library management application built with an **Object-Oriented Design (OOP)** architecture in Java. 

It provides an end-to-end digital ecosystem for modern academic libraries:
- **Zero-Dependency Native Backend**: Runs on Java JDK 17/21 using native HTTP server capabilities without third-party frameworks.
- **Unified Modern Interface**: A web-based desktop application interface with live search, barcode stations, stock indicators, and automated role detection.
- **Multi-Role Circulation**: Separate tailored environments for Students, Faculty, and Library Administrators.
- **Accreditation-Ready**: Features an immutable audit trail, automated fine management, and CSV export capabilities.

---

## 2. Technology Stack

| Layer | Technologies Used |
|---|---|
| **Backend Core** | Java 17/21 LTS (Pure OOP: Inheritance, Polymorphism, Strategy, Facade patterns) |
| **HTTP Engine** | Java Native `com.sun.net.httpserver.HttpServer` (Virtual Thread Executor) |
| **Data & Persistence** | In-memory collections with JSON backup persistence |
| **Frontend UI** | HTML5, Vanilla Modern CSS (Responsive, Glassmorphism design), JavaScript ES6+ |
| **Containerization** | Multi-stage Docker (Eclipse Temurin JDK 21 Alpine) |
| **Cloud Deployment** | Render, Cloudflare Zero Trust Tunnel |

---

## 3. How to Launch & Access the Application

### A. Running Locally on Your Computer

You can launch the system using any of the following methods from the project root:

```bash
# Method 1: Quick Launcher Script
./run_java.sh

# Method 2: Short Alias
./College

# Method 3: Direct Executable JAR
java -jar LibraryManagementSystem.jar

# Method 4: Headless Server Mode (Background)
java -jar LibraryManagementSystem.jar --server 8080 &

# Method 5: Interactive Terminal CLI Mode
java -jar LibraryManagementSystem.jar --cli
```

Once started, open your web browser to:
👉 **`http://localhost:8080`**

---

### B. Accessing Over Local Wi-Fi / Hotspot *(No Internet Needed)*

To let other devices (phones, laptops, tablets) in the same room or lab access the library:

1. Connect both your computer and the other devices to the **same Wi-Fi router** or your phone's personal hotspot.
2. Find your computer's local IP address (e.g., `192.168.1.X` or `10.X.X.X`).
3. Other users simply open their browser and visit:
   ```
   http://<YOUR_LOCAL_IP>:8080
   ```
- **Benefit**: Works 100% offline, ultra-fast response, no account or service signups needed.

---

### C. Accessing from Anywhere via Cloud (Render)

Deploying to the cloud allows the system to run **24/7 permanently** without needing your personal computer to stay open.

1. Push your repository to GitHub.
2. Log into [Render](https://render.com) and create a **New Web Service** pointing to your repository.
3. Select **Docker** as the runtime (Render auto-detects `Dockerfile` and `render.yaml`).
4. Select the **Free** tier and click **Deploy**.
5. You receive a permanent public HTTPS address:
   ```
   https://<your-service-name>.onrender.com
   ```

*(See `RENDER_DEPLOYMENT.md` for detailed cloud deployment steps).*

---

### D. Instant Tunnel (Cloudflare / Pinggy)

For quick temporary remote demonstrations:

- **Cloudflare Tunnel**:
  ```bash
  ./run_cloudflare.sh
  ```
  *(Generates a temporary public `https://*.trycloudflare.com` URL in your terminal).*

- **Pinggy (Zero-install SSH Tunnel)**:
  ```bash
  ssh -p 443 -R0:localhost:8080 qr@a.pinggy.io
  ```
  *(Displays an HTTPS URL and a QR Code directly inside your terminal for instant smartphone scanning).*

---

## 4. User Authentication & Portals

### Unified Login

The system features a **single, unified login window**. Users do not need to manually choose whether they are a Student, Librarian, or Faculty:
- The system inspects the ID format automatically upon entry.
- Directs the user immediately to their role-specific portal.

### Default Accounts & Credentials

| Role | User ID / Username | Password | Access Level |
|---|---|---|---|
| **Senior Librarian** | `LIB-001` | `password123` | Full administrative, inventory & circulation controls |
| **Assistant Librarian** | `LIB-102` | `password123` | Full circulation, member directory & catalog controls |
| **Student (CS)** | `2026-CS-001` | `password123` | Book search, loan management, digital ID card |
| **Student (EC)** | `2026-EC-014` | `password123` | Book search, loan management, digital ID card |
| **Student (ME)** | `2026-ME-022` | `password123` | Book search, loan management, digital ID card |
| **Student (Civil)** | `2026-CE-045` | `password123` | Book search, loan management, digital ID card |
| **Faculty Member** | `FAC-102` | `password123` | Extended borrowing quota & 30-day loan periods |

---

## 5. Librarian / Staff Guide

### Dashboard & Live Stat Counters
Upon signing in as a Librarian, the top dashboard displays 4 live stat cards:
1. **Book Titles**: Total unique titles registered in the catalogue.
2. **Physical Copies**: Total individual accessioned copies across all titles.
3. **Students**: Active registered student accounts.
4. **Librarians**: Registered library staff accounts.

> **Tip**: All stat cards are clickable shortcuts:
> - Clicking **Book Titles** or **Physical Copies** opens the **Book Inventory** tab.
> - Clicking **Students** or **Librarians** opens the **User Management** tab.

---

### Book Inventory & Cataloguing

Click the **Inventory** tab to manage books:

1. **Live Search**: Type any title, author name, ISBN, or shelf location in the search bar for instant filtering.
2. **Category Filter Pills**: One-click filters:
   - *All Books*
   - *In Stock Only* (shows only books with available copies)
   - *Issued / Checked Out*
   - Department categories (*Computer Science*, *Electronics*, *Mechanical*, etc.)
3. **Adding a New Book Title**:
   - Click the **"➕ Add New Book"** button at the top.
   - Enter the Title, Author, ISBN, Category, and Shelf Code.
   - Click **Save Book**.
4. **Stock Indicators**:
   - Every card has a color-coded status bar: **Green** represents available copies; **Navy** represents issued copies.
   - Cards display status badges: `✓ IN STOCK` or `⚠️ ALL ISSUED`.

---

### Physical Copy Accession Management

Each book title can have multiple physical copies with distinct barcodes.

1. Click **"View Copies →"** on any book card to open the **Physical Copy Accession Modal**.
2. **Copy Details**:
   - Displays each physical copy ID (e.g. `LIB-002-001`, `LIB-002-002`).
   - Displays status: `AVAILABLE`, `ISSUED`, `RESERVED`, or `MAINTENANCE`.
   - If issued, displays borrower details, due dates, and a **"🔄 Renew Loan"** button.
3. **Registering a New Copy**:
   - Click **"➕ Add Copy"** to instantly generate an accession copy with a unique barcode.

---

### Circulation Desk (Issue, Return, Renew)

#### Issuing a Book:
1. Navigate to the **Circulation** tab.
2. Select **"Issue Book"**.
3. Enter or scan:
   - **Student ID** (e.g., `2026-CS-001`).
   - **Copy Barcode** (e.g., `LIB-001-002`).
4. Click **Complete Checkout**.
5. The system verifies borrowing quotas and overdue fines automatically. If clear, the loan is recorded with an automated 14-day return deadline.

#### Returning a Book:
1. Select **"Return Book"**.
2. Enter the copy barcode.
3. The system checks if the book is overdue:
   - If on time: Mark copy as `AVAILABLE`.
   - If overdue: System automatically calculates the fine based on days delayed.
4. Select payment method (Cash, UPI, Online Waiver) to settle any fines and complete return.

#### Renewing an Active Loan:
- Active loans can be extended by clicking **Renew** in the loan list or copy modal, resetting the due date by another loan term.

---

### User Management & Search

Click the **Users** tab to view all library members:
1. **Live Member Search**: Type in the top search bar to filter members by:
   - Full Name
   - Student ID / Username
   - Department
   - Email address
2. **Search Highlight**: Matching terms are automatically highlighted in gold/yellow for rapid visual identification.
3. **Role Filters**: Filter by *Students*, *Faculty*, or *Librarians*.
4. **Member Details**: Inspect active loans, overdue fines, and contact details.

---

### Reports, Logs & Analytics

1. **Circulation Logs**: Chronological table of all book issues, returns, and renewals.
2. **Export to CSV**: Click **"📥 Export CSV"** to generate a spreadsheet report for accreditation reviews.
3. **Analytics**: Visual distribution charts showing checkout activity by department and category.
4. **Audit Trail**: Immutable cryptographic ledger recording every login, fine collection, and inventory modification.

---

### Library Policy & Fine Rules

In the **Rules** tab, staff can configure:
- **Daily Overdue Fine Rate**: Standard fine per day (e.g., ₹2.00 / day).
- **Loan Duration Limits**: Standard student loan period (e.g., 14 days) vs. faculty loan period (e.g., 30 days).
- **Maximum Borrow Limits**: Maximum concurrent books a student can borrow (default: 3 books).

---

## 6. Student & Member Guide

### Browsing & Searching Books
1. Log in with your Student ID and password.
2. The **Catalogue** page displays all books in the library.
3. Search by title, author, or subject area.
4. Check real-time availability:
   - `IN STOCK`: Copy available on shelf.
   - `ALL ISSUED`: Check due date or place a hold.

### Viewing Active Loans & Renewals
1. Navigate to the **"My Books"** tab.
2. View your currently borrowed books, issue dates, and due dates.
3. If eligible, click **"Renew"** before the due date to extend your loan period.
4. Check outstanding fine amounts (if any).

### Digital Library ID Card
1. Click the **Profile** tab.
2. Displays your **Official Digital Library Card**:
   - Student Name & Photo placeholder
   - Matriculation / Student ID
   - Department & Graduation Year
   - Scannable Personal Barcode for rapid counter checkouts

---

## 7. System Architecture & Design Patterns

The backend follows solid **Object-Oriented Design Principles (SOLID)**:

```
┌────────────────────────────────────────────────────────┐
│                   LibraryHttpServer                    │
│   (Zero-dependency HTTP Handler & REST API Router)     │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                     LibrarySystem                      │
│             (Facade Pattern Coordinator)               │
└─────┬──────────────┬──────────────┬──────────────┬─────┘
      │              │              │              │
┌─────▼────┐   ┌─────▼────┐   ┌─────▼────┐   ┌─────▼────┐
│User      │   │Catalog   │   │Circula-  │   │Fine      │
│Service   │   │Service   │   │tionSvc   │   │Service   │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

1. **Facade Pattern** (`LibrarySystem.java`): Unified entry point simplifying interactions across catalog, circulation, fine, and user subsystems.
2. **Strategy Pattern** (`FineCalculator.java` / `PaymentProcessor.java`): Pluggable algorithms for dynamic fine calculation and multiple payment settlement modes (Cash, UPI, Online).
3. **Inheritance & Polymorphism** (`User.java` ➔ `Student.java`, `Faculty.java`, `Librarian.java`): Dynamic method dispatch for quota and policy enforcement.
4. **Observer / Audit Trail** (`AuditService.java`): Immutable chronological event streaming for all security-sensitive operations.

---

## 8. Frequently Asked Questions (FAQ)

#### Q: How do I access the library from my mobile phone?
**A**: Ensure your phone is connected to the same Wi-Fi as the host computer, then open `http://<HOST_IP>:8080` in your mobile browser. Alternatively, access via the public cloud link.

#### Q: What if a student loses their library card?
**A**: Students can present their **Digital Library ID Card** directly on their smartphone screen via the Profile tab. The barcode can be scanned directly from the screen at the circulation desk.

#### Q: Can the application run if the internet goes down?
**A**: Yes! The entire core application, embedded web server, and local database are 100% self-contained and run locally on your LAN without requiring internet access.

#### Q: Where are configuration and inventory stored?
**A**: Data is managed in high-speed in-memory collections and persisted to JSON files under the project directory, ensuring zero database installation overhead.

---

*College Library Management System — Operational Manual*
