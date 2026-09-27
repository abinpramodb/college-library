# 📋 College Library Management System — What's New

> Changes made on **September 27, 2026** — additions not covered in `PROJECT_FULL_DETAILS.md`.

---

## 1. Unified Login

- Single login screen for **both students and librarians** — no role selector or separate pages.
- Role is auto-detected from the entered ID.
- Student IDs (e.g. `2026CE045`) → Student Portal.
- Librarian IDs (e.g. `LIB-001`, `LIB-102`, or alias `LIBRARIAN`) → Librarian Console.
- Faculty IDs (e.g. `FAC-102`) → Student Portal with faculty access.

---

## 2. No Separate Admin Role

- **Admin and Librarian are the same role** throughout the entire UI.
- All labels, badges, role pills, and tab titles show **"Librarian"** only.
- The word "Admin" does not appear anywhere visible in the interface.

---

## 3. Book Inventory Tab

- **Live search bar** — real-time filtering by title, author, ISBN, shelf code, or category.
- **Filter pills** — All Books · In Stock Only · Issued / Checked Out · Computer Science · Electronics · Mechanical.
- **Live stock header** — shows total Available / Issued / Copies count at a glance.
- **Stock indicator bar** — green/navy fill bar showing available vs issued ratio on every book card.
- **Stock badge** — green ✓ IN STOCK or red ⚠️ ALL ISSUED on each card.
- **Per-book action buttons** on every card:
  - ➕ Add Copy — registers a new physical accession copy with auto-generated barcode.
  - View Copies → — opens the Physical Copy modal.
  - ⚡ Issue — quick-issue shortcut.
  - 🗑️ Delete — removes the book title from the catalogue.
- **Add New Book** button at the top to register new titles.

---

## 4. Physical Copy Accession Modal

- Shows every individual physical copy of a book with its barcode, shelf location, and status.
- Copies auto-generated on first open using format `LIB-{bookId}-{copyNum}` (e.g. `LIB-002-003`).
- Status badges: AVAILABLE · ISSUED · RESERVED · DAMAGED · LOST.
- Issued copies show borrower name, due date, and a **🔄 Renew Loan** button.
- ➕ Add Copy button inside the modal to register new accession barcodes.

---

## 5. Dashboard — Live Stat Cards

- **Book Titles** — live count, updates when books are added or deleted.
- **Physical Copies** — live count, updates when copies are added.
- **Students** — live count from actual user list.
- **Librarians** — live count from actual user list.
- All four cards are **clickable**:
  - Book Titles & Physical Copies → Book Inventory tab.
  - Students & Librarians → User Management tab.

---

## 6. User Management — Live Search

- **Search bar** at the top of the Users tab.
- Searches by: name, ID/username, department, or email — in real time.
- Works **combined** with role filter pills (e.g. search "Kumar" within "Students" only).
- **Yellow highlight** on the matching part of the name in results.
- **✕ Clear button** resets both search and role filter back to All.

---

## 7. Profile Page — Action Menu

- Action items (Backup Database · Security Keys & 2FA · Audit Trail · Sign Out) now appear as a **clean white card list** with:
  - Icon pill on the left.
  - Bold title + grey subtitle.
  - Animated → arrow that slides right on hover.
- Removed from profile page:
  - "Core Engine — Java 21 LTS" card.
  - "Audit Ledger — Immutable" card.

---

## 8. Cloudflare Public Access

- The app is accessible from **any device on any network** via Cloudflare Tunnel.
- Run `./run_cloudflare.sh` in the terminal to get a live public HTTPS URL.
- Current session URL: `https://groundwater-chicken-expects-packing.trycloudflare.com`
- ⚠️ URL changes every time the tunnel is restarted — always copy from the terminal output.
- Local access (same Mac): `http://localhost:8080`

---

## 9. Backend Cloud Deployment & 24/7 Hosting (Render)

- **Dynamic Port Binding**:
  - `Main.java` updated to read the `PORT` environment variable (`System.getenv("PORT")`) and `--port <number>` CLI flag.
  - Automatically binds to cloud-assigned ports (e.g. Render port `10000`) without manual reconfiguration.
- **Headless Cloud Auto-Detection**:
  - Automatically detects Linux and headless server environments (`isHeadless()` / Linux OS).
  - Switches to dedicated headless server mode without attempting to launch macOS desktop windows.
- **Production Docker Containerization**:
  - Added multi-stage [`Dockerfile`](file:///Users/abinpramodb/Downloads/library/Dockerfile) powered by Eclipse Temurin JDK 21 Alpine.
  - Compiles source code, packages assets, and runs on a lightweight JRE runtime.
- **Render Blueprint Configuration**:
  - Added [`render.yaml`](file:///Users/abinpramodb/Downloads/library/render.yaml) for 1-click cloud service deployment on Render Free Tier.
  - Configured automated health checks against `/api/stats`.
- **GitHub Version Control & Cloud Sync**:
  - Codebase tracked with Git and configured for cloud repository sync.
  - Recompiled standalone [`LibraryManagementSystem.jar`](file:///Users/abinpramodb/Downloads/library/LibraryManagementSystem.jar) with cloud port support.
- **Deployment Documentation**:
  - Step-by-step cloud deployment instructions provided in [`RENDER_DEPLOYMENT.md`](file:///Users/abinpramodb/Downloads/library/RENDER_DEPLOYMENT.md).

---

*Updated: 2026-09-27*

