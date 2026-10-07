# 📁 College Library Management System — Project Structure

> A comprehensive map of all files, directories, modules, and architectural layers in the repository.

---

## 🌳 High-Level Directory Tree

```
college-library/
├── 📄 SimpleLibrary.java              # 🟢 Standalone lightweight single-file terminal CLI (< 250 lines)
├── 📄 LibraryTerminalApp.java          # 🟢 Standalone enterprise single-file terminal CLI
├── 📄 LibraryManagementSystem.java     # 🟢 Full monolithic distribution file
├── 📦 LibraryManagementSystem.jar      # ⚡ Standalone pre-compiled executable Java JAR
├── 🚀 Launch Scripts
│   ├── run_terminal.sh                # Interactive terminal runner (for SimpleLibrary / TerminalApp)
│   ├── run_java.sh                    # Desktop launcher script
│   ├── College                        # Short alias launcher
│   └── run_cloudflare.sh              # Cloudflare Zero Trust public HTTPS tunnel
├── ☁️ Cloud & Deployment
│   ├── Dockerfile                     # Multi-stage Eclipse Temurin JDK 21 Alpine container
│   ├── render.yaml                    # Render cloud deployment blueprint
│   └── college-library.service        # Linux systemd daemon service definition
├── 📂 java-lms/                       # ☕ Full Enterprise Modular Source Code
│   ├── build_and_run.sh               # Native compile & packaging script
│   ├── src/
│   │   ├── module-info.java           # Java Platform Module System (JPMS) descriptor
│   │   └── com/library/
│   │       ├── Main.java              # Application main entrypoint & CLI routing
│   │       ├── LibrarySystem.java     # Central Facade Pattern orchestrator
│   │       ├── LibraryHttpServer.java # Zero-dependency HTTP server & REST API router
│   │       ├── models/                # Domain entities (Encapsulation & Inheritance)
│   │       │   ├── User.java          # Abstract base user class
│   │       │   ├── Student.java       # Student entity (quota: 3-4 books, 14 days)
│   │       │   ├── Faculty.java       # Faculty entity (quota: 5 books, 30 days)
│   │       │   ├── Librarian.java     # Library administrator entity
│   │       │   ├── Book.java          # Logical book title entity
│   │       │   ├── BookCopy.java      # Physical shelf copy with barcode
│   │       │   ├── BorrowRecord.java  # Active & archived loan records
│   │       │   ├── Fine.java          # Overdue penalty tracking
│   │       │   ├── BorrowPolicy.java  # Dynamic borrowing quotas & daily rates
│   │       │   └── AuditLog.java      # Immutable audit trail event
│   │       ├── enums/                 # Strongly-typed system states
│   │       │   ├── Role.java          # STUDENT, FACULTY, LIBRARIAN
│   │       │   ├── BookStatus.java    # AVAILABLE, ISSUED, RESERVED, DAMAGED, LOST
│   │       │   ├── PaymentMethod.java # CASH, UPI, ONLINE, WAIVED
│   │       │   └── TransactionType.java# ISSUE, RETURN, RENEW, FINE_PAID
│   │       ├── interfaces/            # OOP abstraction contracts
│   │       │   ├── Searchable.java    # Universal keyword search contract
│   │       │   ├── Auditable.java     # Audit serialization contract
│   │       │   ├── FineCalculator.java# Strategy pattern contract for fines
│   │       │   └── PaymentProcessor.java # Strategy pattern contract for payments
│   │       ├── strategies/            # Concrete Strategy implementations
│   │       │   ├── StandardFineCalculator.java
│   │       │   ├── UpiPaymentProcessor.java
│   │       │   └── CashPaymentProcessor.java
│   │       ├── services/              # Core business logic subsystems
│   │       │   ├── CatalogService.java    # Book & physical copy management
│   │       │   ├── CirculationService.java# Loan checkouts, checkins & renewals
│   │       │   ├── UserService.java       # Member authentication & directory
│   │       │   ├── FineService.java       # Overdue penalty computation
│   │       │   └── AuditService.java      # Cryptographic event logging
│   │       └── ui/
│   │           ├── ConsoleUI.java     # Interactive terminal menus
│   │           └── TestRunner.java    # Self-verifying automated unit tests
│   └── resources/
│       └── web/
│           └── index.html             # Encapsulated responsive UI (HTML5/CSS3/ES6)
├── 📂 credentials/                     # 🔐 Account Management
│   ├── accounts.json                  # Seeded member accounts database
│   ├── passwords.txt                  # Quick credentials reference
│   └── CREDENTIALS.md                 # Role permissions & access matrix
└── 📚 Documentation Suite
    ├── PROJECT_STRUCTURE.md           # 📍 This project directory guide
    ├── REPORT_USER_MANUAL.md          # 🎓 Academic report-ready cloud user manual
    ├── USER_MANUAL.md                 # 📖 Comprehensive end-user operation manual
    ├── PROJECT_REPORT.md              # 📝 Formal B.Tech (KTU) semester project report
    ├── PROJECT_FULL_DETAILS.md        # 📋 Complete architectural specification
    ├── PROJECT_UPDATES.md             # 🔄 Changelog of latest feature additions
    ├── RENDER_DEPLOYMENT.md           # ☁️ 24/7 Render cloud hosting instructions
    └── OOP_TUTORIAL.md                # 💡 Viva voce & Java OOP concepts breakdown
```

---

## 🧩 Detailed Component Breakdown

### 1. Standalone Single-File Java Editions

| File | Purpose | Size / Complexity |
|---|---|---|
| **`SimpleLibrary.java`** | Beginner-friendly, ultra-clean single Java file for viva demonstrations and quick terminal usage. Has Student Portal and Librarian Console with zero external dependencies. | ~250 lines |
| **`LibraryTerminalApp.java`** | Enterprise single Java file containing the complete OOP architecture (Services, Models, Strategies, Facade) running 100% in terminal. | ~980 lines |
| **`LibraryManagementSystem.java`** | Complete single-file distribution bundle containing both CLI and embedded HTTP web server capabilities. | ~2800 lines |

---

### 2. Enterprise Modular Codebase (`java-lms/src/com/library/`)

The modular source code adheres strictly to **SOLID design principles**:

- **Central Facade (`LibrarySystem.java`)**: The single entrypoint coordinating cross-service operations between catalog, circulation, users, fines, and audit trails.
- **Embedded Web Server (`LibraryHttpServer.java`)**: Uses Java 21's native `com.sun.net.httpserver.HttpServer` with Virtual Threads to route REST endpoints (`/api/books`, `/api/issue`, `/api/return`, `/api/stats`) and serve static web assets.
- **Subsystem Services (`services/`)**:
  - `CatalogService`: Title inventory, accession barcodes, multi-field search.
  - `CirculationService`: Issue/return limits, overdue status, loan extensions.
  - `UserService`: Directory lookup, role validation, authentication.
  - `FineService`: Daily overdue penalties and settlement accounting.
  - `AuditService`: Chronological security ledger for inspections.
- **Design Pattern Strategies (`strategies/`)**: Pluggable fine calculators (`StandardFineCalculator`) and settlement processors (`UpiPaymentProcessor`, `CashPaymentProcessor`).

---

### 3. Frontend & User Interface (`java-lms/resources/web/index.html`)

- **Single-Page Application (SPA)**: Encapsulated modern glassmorphism design with responsive support for mobile phones, tablets, and desktops.
- **Student Portal**: Book catalogue browsing, live stock fill bars, loan extension triggers, and scannable digital ID cards.
- **Librarian Console**: Interactive stat cards, copy accession modal, live barcode lookup, CSV export, and rule configuration.

---

### 4. Cloud & Deployment Files

- **`Dockerfile`**: Production multi-stage container build based on `eclipse-temurin:21-alpine`. Automatically compiles the Java source code and packages a minimal JRE runtime image.
- **`render.yaml`**: Infrastructure-as-Code blueprint for deploying on Render's free tier with automated health checks on `/api/stats`.
- **`college-library.service`**: Linux `systemd` daemon unit for bare-metal VPS hosting.

---

### 5. Documentation Suite

- **`REPORT_USER_MANUAL.md`**: Clean, concise chapter specifically formatted for copying into your college project report.
- **`USER_MANUAL.md`**: Complete step-by-step operations handbook for library staff and students.
- **`PROJECT_REPORT.md`**: Full formal B.Tech course project report with Certificate, Declaration, Modules, System Architecture, and Viva Voce Q&A.
- **`PROJECT_UPDATES.md`**: Record of all modern UI/UX and architectural enhancements.
- **`OOP_TUTORIAL.md`**: Comprehensive revision guide explaining how each OOP concept (Inheritance, Polymorphism, Abstraction, Encapsulation) maps to the actual code.

---

## ⚡ Execution Matrix — Which File to Run?

| Desired Goal | Command to Execute |
|---|---|
| **Simple Interactive Terminal (Recommended)** | `java SimpleLibrary.java` |
| **Advanced Terminal Edition** | `java LibraryTerminalApp.java` *(or `./run_terminal.sh`)* |
| **Local Desktop GUI (Browser mode)** | `./run_java.sh` *(or `./College`)* |
| **Pre-compiled Standalone JAR** | `java -jar LibraryManagementSystem.jar` |
| **Automated Unit Tests** | `java -jar LibraryManagementSystem.jar --test` |
| **Temporary Mobile Internet Tunnel** | `./run_cloudflare.sh` |
| **Production Cloud Website** | Access live Render URL *(see `REPORT_USER_MANUAL.md`)* |
