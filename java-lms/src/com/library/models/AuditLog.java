package com.library.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private int id;
    private LocalDateTime timestamp;
    private String actor;
    private String action;
    private String detail;

    public AuditLog(int id, String actor, String action, String detail) {
        this.id = id;
        this.timestamp = LocalDateTime.now();
        this.actor = actor;
        this.action = action;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return timestamp.format(dtf);
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s: %s", getFormattedTimestamp(), action, actor, detail);
    }
}
