package com.library.services;

import com.library.models.AuditLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AuditService {
    private final List<AuditLog> logs = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger nextLogId = new AtomicInteger(1);

    public AuditService() {
        seedInitialLogs();
    }

    public synchronized AuditLog logAction(String actor, String action, String detail) {
        int id = nextLogId.getAndIncrement();
        AuditLog log = new AuditLog(id, actor, action, detail);
        logs.add(0, log); // Newest first
        return log;
    }

    public List<AuditLog> getRecentLogs(int limit) {
        synchronized (logs) {
            int toIndex = Math.min(logs.size(), limit > 0 ? limit : logs.size());
            return new ArrayList<>(logs.subList(0, toIndex));
        }
    }

    public List<AuditLog> getAllLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }

    private void seedInitialLogs() {
        logAction("Librarian #102", "BOOK_ISSUED", "Issued 'Clean Architecture' [LIB-000602] to Priya Sharma (2026CS142)");
        logAction("Librarian #102", "BOOK_RETURNED", "Returned 'Computer Networks' [LIB-000624] from Rahul Verma (2026CS108)");
        logAction("Librarian #102", "FINE_COLLECTED", "Collected ₹40 fine from Priya Sharma via UPI (TXN-984321)");
        logAction("Librarian #001", "RULE_UPDATED", "Updated Student borrowing policy: Max loan 14 days, fine rate ₹2.00/day");
    }
}
