# College Library Management System (Java Application)

An enterprise-grade, Object-Oriented **Java Desktop Application** (JDK 17/21) for managing college library circulation, member directories, book inventory, laser barcode stations, and administrative policies.

## Project Structure

- `java-lms/src/com/library/Main.java` - Main entrypoint for launching the application
- `java-lms/src/com/library/LibrarySystem.java` - Central OOP Domain Facade
- `java-lms/src/com/library/LibraryHttpServer.java` - Embedded lightweight server for desktop UI
- `java-lms/src/com/library/models/` - Domain entities (`Book`, `BookCopy`, `User`, `Student`, `Librarian`, `BorrowRecord`, `Fine`, etc.)
- `java-lms/src/com/library/services/` - Core business logic (`CatalogService`, `CirculationService`, `FineService`, `UserService`, `AuditService`)
- `java-lms/src/com/library/strategies/` - Design patterns (`FineCalculator`, `PaymentProcessor`)
- `java-lms/src/com/library/ui/ConsoleUI.java` - Terminal CLI interface
- `java-lms/src/com/library/ui/TestRunner.java` - Self-verifying OOP unit tests
- `java-lms/resources/web/index.html` - Encapsulated UI assets
- `java-lms/build_and_run.sh` - Compilation and build script
- `LibraryManagementSystem.jar` - Standalone executable Java JAR
- `College Library.app` - macOS desktop application launcher bundle
- `run_java.sh` / `College` - Quick launch scripts

## How to Run

```bash
# Option 1: Quick launcher
./run_java.sh

# Option 2: Short alias
./College

# Option 3: Standard JAR execution
java -jar LibraryManagementSystem.jar

# Option 4: Terminal CLI mode
java -jar LibraryManagementSystem.jar --cli

# Option 5: Cloudflare Public HTTPS Tunnel
./run_cloudflare.sh
```
