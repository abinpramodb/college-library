package com.library.interfaces;

import com.library.models.AuditLog;

public interface Auditable {
    AuditLog createAuditRecord(String actor, String actionDetails);
}
