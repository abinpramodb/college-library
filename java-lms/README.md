# 📚 College Library Management System (Desktop Application)

An enterprise-grade, Object-Oriented **Desktop Application** powered by a robust Java backend (JDK 17/21) and presenting a pixel-perfect, modern standalone application window with dedicated portals for Students, Librarians, and Administrators.

---

## 🏛️ Object-Oriented Architecture & Design Patterns

The Java backend has zero external dependencies and strictly implements core OOP patterns:

### 1. Encapsulation & Inheritance Hierarchy
- **`User` (Abstract Base Class)**: Encapsulates member identification, credentials, contact info, and defines polymorphic abstract methods:
  - `Student extends User`: Tracks register numbers, semester, borrow quotas, active loans, and overdue fine balances.
  - `Librarian extends User`: Circulation desk workstation permissions, shifts, and inventory management.
  - `Faculty extends User`: Extended loan periods and research quotas.
  - `Admin extends User`: Global policy and auditing authority.

### 2. Strategy Pattern
- **`FineCalculator` Interface**:
  - `StandardFineCalculator implements FineCalculator`: Computes daily overdue fees based on due dates and return timestamps.
- **`PaymentProcessor` Interface**:
  - `UpiPaymentProcessor implements PaymentProcessor`: Instant simulated UPI QR settlement.
  - `CashPaymentProcessor implements PaymentProcessor`: Librarian desk receipt issuance.
  - `OnlinePaymentProcessor implements PaymentProcessor`: Net banking / Card verification.

### 3. Facade & Interface Segregation
- **`LibrarySystem` (Central Facade)**: Coordinates domain services cleanly.
- **`Searchable`**: Contract implemented by `Book` and `User` for unified querying across titles, authors, and register numbers.
- **`Auditable`**: Contract ensuring every circulation action is recorded in an immutable audit trail.

### 4. Domain Models & Services
- **Models**: `Book`, `BookCopy` (physical barcode accession unit), `BorrowRecord`, `Reservation`, `Fine`, `BorrowPolicy`, `AuditLog`.
- **Services**:
  - `CatalogService`: Manages book inventory, physical copies, auto-generates accession barcodes (`LIB-000600+`).
  - `CirculationService`: Issues books, checks out physical barcodes, processes returns, manages renewals, and enforces quota limits.
  - `FineService`: Handles fine calculation, payment settlement, and revenue tracking.
  - `UserService`: Directory of student and staff accounts.
  - `AuditService`: Comprehensive immutable audit trail.

### 5. Application Features & Operations
- **Student Portal**:
  - Identity: **Student** (`2026CE045 · Computer Engineering`).
  - Digital Student ID Card with dynamic barcode.
  - Active loans overview with renewal notices.
  - Fine balance summary with UPI / Cash / Card settlement modal.
  - Complete searchable catalog with **Reserve Book** actions.
- **Librarian Circulation Desk**:
  - Real-time KPI summary cards (Total Books, In Circulation, Overdue, Collections).
  - **Issue Book**: Handheld laser barcode scanner station with quick-fill sample barcodes.
  - **Return Book**: Accession barcode check-in station with automatic fine assessment.
  - **Add Book**: Form with Google Books API ISBN auto-fill + physical accession barcode generator.
  - **Copies & Inventory**: Detailed copy-level shelf tracking and stock counts.
  - **Members Directory**: Student and staff account records.
- **Admin Configuration**:
  - Editable borrowing rules (Loan duration, Max books, Daily fine rate).
  - Real-time immutable audit trail log table.

---

## 🚀 How to Run

### Option 1: Using the Launcher Script (Recommended)
From the root workspace directory:
```bash
./run_java.sh
```
Or:
```bash
./College
```

### Option 2: Running the Packaged JAR
```bash
java -jar LibraryManagementSystem.jar
```

### Option 3: Terminal CLI Mode
```bash
java -jar LibraryManagementSystem.jar --cli
```
