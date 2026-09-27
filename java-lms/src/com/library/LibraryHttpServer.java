package com.library;

import com.library.enums.PaymentMethod;
import com.library.enums.Role;
import com.library.models.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

public class LibraryHttpServer {
    private final LibrarySystem system;
    private final int port;
    private HttpServer server;
    private final Path webRoot;

    public LibraryHttpServer(LibrarySystem system, int port, Path webRoot) {
        this.system = system;
        this.port = port;
        this.webRoot = webRoot;
    }

    private int activePort;

    public void start() throws IOException {
        activePort = port;
        IOException lastEx = null;
        for (int p = port; p < port + 10; p++) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                activePort = p;
                lastEx = null;
                break;
            } catch (IOException e) {
                lastEx = e;
            }
        }
        if (server == null && lastEx != null) {
            throw lastEx;
        }

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // API Endpoints
        server.createContext("/api/books", new BooksHandler());
        server.createContext("/api/copies", new CopiesHandler());
        server.createContext("/api/copies/add", new CopiesHandler());
        server.createContext("/api/issue", new IssueHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.createContext("/api/renew", new RenewHandler());
        server.createContext("/api/fines", new FinesHandler());
        server.createContext("/api/members", new MembersHandler());
        server.createContext("/api/users", new MembersHandler());
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/audit", new AuditHandler());
        server.createContext("/api/rules", new RulesHandler());
        server.createContext("/api/login", new LoginHandler());

        // Static Web Files (Serves the exact UI HTML/CSS/JS)
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println(" Library HTTP & REST Server running at: http://localhost:" + activePort + "/");
    }

    public int getActivePort() {
        return activePort;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --- Handlers ---

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            Path file = null;

            if (path.equals("/") || path.equals("/index.html") || path.equals("/library.html") || path.isEmpty()) {
                Path[] candidates = {
                    webRoot.resolve("java-lms/resources/web/index.html"),
                    webRoot.resolve("resources/web/index.html"),
                    Paths.get("java-lms/resources/web/index.html").toAbsolutePath(),
                    Paths.get("resources/web/index.html").toAbsolutePath()
                };
                for (Path c : candidates) {
                    if (Files.exists(c)) {
                        file = c;
                        break;
                    }
                }
            } else {
                Path candidate = webRoot.resolve(path.startsWith("/") ? path.substring(1) : path).normalize();
                if (Files.exists(candidate) && !Files.isDirectory(candidate)) {
                    file = candidate;
                }
            }

            if (file == null || !Files.exists(file)) {
                // Fallback to resources index.html
                file = webRoot.resolve("java-lms/resources/web/index.html");
            }

            byte[] bytes = null;
            String contentType = "text/html; charset=UTF-8";

            if (file != null && Files.exists(file)) {
                bytes = Files.readAllBytes(file);
                if (file.toString().endsWith(".css")) contentType = "text/css; charset=UTF-8";
                else if (file.toString().endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
                else if (file.toString().endsWith(".json")) contentType = "application/json; charset=UTF-8";
            } else {
                try (InputStream is = getClass().getResourceAsStream("/resources/web/index.html")) {
                    if (is != null) {
                        bytes = is.readAllBytes();
                    }
                } catch (Exception ignored) {}
            }

            if (bytes != null) {
                sendCors(exchange);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String notFound = "<h1>404 File Not Found</h1>";
                sendCors(exchange);
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    private class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                List<Book> books = system.getCatalogService().getAllBooks();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < books.size(); i++) {
                    Book b = books.get(i);
                    json.append(bookToJson(b));
                    if (i < books.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);

                String title = map.getOrDefault("title", "Untitled Book");
                String author = map.getOrDefault("author", "Unknown Author");
                String isbn = map.getOrDefault("isbn", "978-0000000000");
                String category = map.getOrDefault("category", "Computer Science");
                String edition = map.getOrDefault("edition", "1st");
                String publisher = map.getOrDefault("publisher", "Academic Press");
                String shelf = map.getOrDefault("shelf", "CS-B-01");
                String cover = map.getOrDefault("cover", "bg-blue-900");
                int copies = 3;
                try {
                    copies = Integer.parseInt(map.getOrDefault("copies", "3"));
                } catch (NumberFormatException ignored) {}

                Book newBook = system.addBook(title, author, isbn, category, edition, publisher, shelf, cover, copies, "Librarian #102");
                sendJson(exchange, 201, bookToJson(newBook));
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class CopiesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                int bookId = 1;
                try {
                    bookId = Integer.parseInt(map.getOrDefault("bookId", "1"));
                } catch (Exception ignored) {}
                String barcode = map.get("barcode");
                String shelf = map.get("shelf");

                BookCopy copy = system.getCatalogService().addCopy(bookId, barcode, shelf);
                if (copy != null) {
                    system.getAuditService().logAction("Librarian #102", "COPY_ADDED",
                            String.format("Added physical copy %s for book #%d (Shelf: %s)", copy.getBarcode(), bookId, copy.getShelfLocation()));
                    sendJson(exchange, 200, String.format("{\"success\":true,\"barcode\":\"%s\",\"shelf\":\"%s\"}", copy.getBarcode(), copy.getShelfLocation()));
                } else {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"Book not found\"}");
                }
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            int bookId = 1;
            if (query != null && query.contains("bookId=")) {
                try {
                    bookId = Integer.parseInt(query.split("bookId=")[1].split("&")[0]);
                } catch (Exception ignored) {}
            }

            List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bookId);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < copies.size(); i++) {
                BookCopy c = copies.get(i);
                json.append(String.format("{\"barcode\":\"%s\",\"bookId\":%d,\"status\":\"%s\",\"shelf\":\"%s\",\"borrower\":\"%s\"}",
                        c.getBarcode(), c.getBookId(), c.getStatus().name(), c.getShelfLocation(),
                        c.getBorrowerId() != null ? c.getBorrowerId() : ""));
                if (i < copies.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class IssueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String studentId = map.getOrDefault("studentId", "2026CS142");
                String barcode = map.get("barcode");
                String bookIdStr = map.get("bookId");

                // If barcode not provided but bookId provided, pick first available copy
                if ((barcode == null || barcode.isBlank()) && bookIdStr != null) {
                    try {
                        int bId = Integer.parseInt(bookIdStr);
                        List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bId);
                        for (BookCopy c : copies) {
                            if (c.isAvailable()) {
                                barcode = c.getBarcode();
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (barcode == null || barcode.isBlank()) {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"No available copy found or barcode missing\"}");
                    return;
                }

                try {
                    BorrowRecord record = system.issueBook(studentId, barcode, "Librarian #102");
                    sendJson(exchange, 200, String.format("{\"success\":true,\"message\":\"Book issued successfully\",\"recordId\":%d,\"barcode\":\"%s\",\"dueDate\":\"%s\"}",
                            record.getRecordId(), record.getCopyBarcode(), record.getDueDate()));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String barcode = map.get("barcode");

                // If bookId provided instead, pick first issued copy
                if ((barcode == null || barcode.isBlank()) && map.get("bookId") != null) {
                    try {
                        int bId = Integer.parseInt(map.get("bookId"));
                        List<BookCopy> copies = system.getCatalogService().getCopiesForBook(bId);
                        for (BookCopy c : copies) {
                            if (!c.isAvailable()) {
                                barcode = c.getBarcode();
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (barcode == null || barcode.isBlank()) {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"Barcode required to return book\"}");
                    return;
                }

                try {
                    BorrowRecord record = system.returnBook(barcode, "Librarian #102");
                    sendJson(exchange, 200, String.format("{\"success\":true,\"message\":\"Book returned successfully\",\"fine\":%.2f,\"studentId\":\"%s\"}",
                            record.getFineAccrued(), record.getStudentId()));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class RenewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                int recordId = Integer.parseInt(map.getOrDefault("recordId", "1"));
                try {
                    boolean ok = system.renewBook(recordId, "Student");
                    sendJson(exchange, 200, String.format("{\"success\":%b,\"message\":\"Loan renewed successfully\"}", ok));
                } catch (Exception e) {
                    sendJson(exchange, 400, String.format("{\"success\":false,\"error\":\"%s\"}", escapeJson(e.getMessage())));
                }
            }
        }
    }

    private class FinesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String studentId = map.getOrDefault("studentId", "2026CS142");
                PaymentMethod pm = PaymentMethod.fromString(map.getOrDefault("method", "upi"));
                String ref = map.getOrDefault("reference", "TXN-" + System.currentTimeMillis() % 1000000);

                boolean ok = system.payAllFines(studentId, pm, ref, "Student Online Gateway");
                sendJson(exchange, 200, String.format("{\"success\":%b,\"reference\":\"%s\",\"message\":\"Payment processed successfully\"}", ok, ref));
            } else {
                String query = exchange.getRequestURI().getQuery();
                String studentId = "2026CS142";
                if (query != null && query.contains("studentId=")) {
                    studentId = query.split("studentId=")[1].split("&")[0];
                }
                List<Fine> fines = system.getFineService().getFinesForStudent(studentId);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < fines.size(); i++) {
                    Fine f = fines.get(i);
                    json.append(String.format("{\"id\":%d,\"amount\":%.2f,\"status\":\"%s\",\"book\":\"%s\",\"date\":\"%s\",\"ref\":\"%s\"}",
                            f.getFineId(), f.getAmount(), f.getStatus().name(), f.getBookTitle(),
                            f.getFormattedDate(), f.getTransactionRef() != null ? f.getTransactionRef() : ""));
                    if (i < fines.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(exchange, 200, json.toString());
            }
        }
    }

    private class MembersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod().toUpperCase();
            if ("POST".equals(method) || "PUT".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String origId = map.getOrDefault("origId", map.getOrDefault("id", "")).trim();
                String newId = map.getOrDefault("id", origId).trim();
                String name = map.getOrDefault("name", "").trim();
                String password = map.getOrDefault("password", map.getOrDefault("pass", "password123")).trim();
                String dept = map.getOrDefault("dept", map.getOrDefault("department", "")).trim();
                String roleStr = map.getOrDefault("role", "Student").trim();
                String status = map.getOrDefault("status", "active").trim();

                if (newId.isEmpty() || name.isEmpty()) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"Name and ID are required\"}");
                    return;
                }

                system.getUserService().updateOrRegisterUser(origId, newId, name, password, dept, roleStr, status);
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"User details saved successfully\"}");
                return;
            }

            if ("DELETE".equals(method)) {
                String path = exchange.getRequestURI().getPath();
                String id = path.substring(path.lastIndexOf('/') + 1);
                boolean deleted = system.getUserService().deleteUser(id);
                sendJson(exchange, 200, String.format("{\"success\":%b}", deleted));
                return;
            }

            List<User> users = system.getUserService().getAllUsers();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                double fine = (u instanceof Student s) ? s.getOutstandingFine() : 0.0;
                int borrowed = (u instanceof Student s) ? s.getCurrentBorrowedCount() : 0;
                String role = (u instanceof Admin || u instanceof Librarian) ? "Librarian" : u.getRole().getDisplayName();

                json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"department\":\"%s\",\"active\":%b,\"fine\":%.2f,\"borrowed\":%d}",
                        escapeJson(u.getId()), escapeJson(u.getName()), escapeJson(u.getPassword()), escapeJson(role), escapeJson(u.getDepartment()), u.isActive(), fine, borrowed));
                if (i < users.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, Object> stats = system.getDashboardStats();
            String json = String.format("{\"todayIssues\":%d,\"todayReturns\":%d,\"overdueBooks\":%d,\"finesToday\":%.2f,\"totalBooks\":%d,\"totalMembers\":%d}",
                    stats.get("todayIssues"), stats.get("todayReturns"), stats.get("overdueBooks"),
                    stats.get("fineCollectedToday"), stats.get("totalBooks"), stats.get("totalMembers"));
            sendJson(exchange, 200, json);
        }
    }

    private class AuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            List<AuditLog> logs = system.getAuditService().getRecentLogs(30);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < logs.size(); i++) {
                AuditLog l = logs.get(i);
                json.append(String.format("{\"id\":%d,\"ts\":\"%s\",\"actor\":\"%s\",\"action\":\"%s\",\"detail\":\"%s\"}",
                        l.getId(), l.getFormattedTimestamp(), escapeJson(l.getActor()),
                        escapeJson(l.getAction()), escapeJson(l.getDetail())));
                if (i < logs.size() - 1) json.append(",");
            }
            json.append("]");
            sendJson(exchange, 200, json.toString());
        }
    }

    private class RulesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("PUT".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                Role role = Role.fromString(map.getOrDefault("role", "student"));
                int maxBooks = Integer.parseInt(map.getOrDefault("maxBooks", "4"));
                int loanDays = Integer.parseInt(map.getOrDefault("loanPeriod", "14"));
                int maxRenew = Integer.parseInt(map.getOrDefault("maxRenewals", "2"));
                double fineRate = Double.parseDouble(map.getOrDefault("fineRate", "2.0"));

                system.getCirculationService().updatePolicy(role, maxBooks, loanDays, maxRenew, fineRate);
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Policy updated successfully\"}");
            } else {
                BorrowPolicy sp = system.getCirculationService().getPolicy(Role.STUDENT);
                BorrowPolicy fp = system.getCirculationService().getPolicy(Role.FACULTY);
                String json = String.format("{\"student\":{\"maxBooks\":%d,\"loanPeriod\":%d,\"maxRenewals\":%d,\"fineRate\":%.2f}," +
                                            "\"faculty\":{\"maxBooks\":%d,\"loanPeriod\":%d,\"maxRenewals\":%d,\"fineRate\":%.2f}}",
                        sp.getMaxBooks(), sp.getLoanPeriodDays(), sp.getMaxRenewals(), sp.getDailyFineRate(),
                        fp.getMaxBooks(), fp.getLoanPeriodDays(), fp.getMaxRenewals(), fp.getDailyFineRate());
                sendJson(exchange, 200, json);
            }
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String id = map.getOrDefault("id", map.getOrDefault("username", "")).trim();

                String password = map.getOrDefault("password", map.getOrDefault("pass", "")).trim();

                if (id.isEmpty()) {
                    sendJson(exchange, 400, "{\"success\":false,\"message\":\"ID/Username is required\"}");
                    return;
                }

                Optional<User> userOpt = system.getUserService().getUserById(id);
                if (userOpt.isPresent()) {
                    User u = userOpt.get();
                    if (!password.isEmpty() && !u.getPassword().equals(password)) {
                        sendJson(exchange, 401, "{\"success\":false,\"message\":\"Incorrect password.\"}");
                        return;
                    }
                    boolean isStaff = (u instanceof Admin || u instanceof Librarian);
                    String userRole = isStaff ? "admin" : "student";
                    String roleName = isStaff ? "Librarian" : u.getRole().getDisplayName();
                    sendJson(exchange, 200, String.format(
                        "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"%s\",\"roleType\":\"%s\",\"department\":\"%s\"}}",
                        escapeJson(u.getId()), escapeJson(u.getName()), escapeJson(roleName), userRole, escapeJson(u.getDepartment())
                    ));
                } else {
                    String cleanId = id.toUpperCase();
                    if (cleanId.startsWith("ADM") || cleanId.startsWith("LIB") || cleanId.equals("ADMIN") || cleanId.equals("LIBRARIAN")) {
                        sendJson(exchange, 200, String.format(
                            "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"Librarian\",\"roleType\":\"admin\",\"department\":\"Library Services\"}}",
                            escapeJson(cleanId), "Librarian"
                        ));
                    } else if (cleanId.startsWith("202") || cleanId.contains("CE") || cleanId.contains("CS") || cleanId.contains("EC") || cleanId.contains("ME")) {
                        sendJson(exchange, 200, String.format(
                            "{\"success\":true,\"user\":{\"id\":\"%s\",\"name\":\"%s\",\"role\":\"Student\",\"roleType\":\"student\",\"department\":\"Engineering Department\"}}",
                            escapeJson(cleanId), "Student Member"
                        ));
                    } else {
                        sendJson(exchange, 401, "{\"success\":false,\"message\":\"Account not found. Invalid ID or credentials.\"}");
                    }
                }
            } else {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    // --- Utility Methods ---

    private String bookToJson(Book b) {
        return String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"isbn\":\"%s\",\"edition\":\"%s\"," +
                             "\"category\":\"%s\",\"publisher\":\"%s\",\"available\":%d,\"total\":%d,\"shelf\":\"%s\",\"cover\":\"%s\"}",
                b.getId(), escapeJson(b.getTitle()), escapeJson(b.getAuthor()), b.getIsbn(),
                escapeJson(b.getEdition()), escapeJson(b.getCategory()), escapeJson(b.getPublisher()),
                b.getAvailableCopies(), b.getTotalCopies(), escapeJson(b.getShelfLocation()), b.getCoverStyle());
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private Map<String, String> parseJsonSimple(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;
        String clean = body.trim();
        if (clean.startsWith("{") && clean.endsWith("}")) {
            clean = clean.substring(1, clean.length() - 1);
        }

        // Split by top-level commas
        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(key, val);
            }
        }
        return map;
    }
}
