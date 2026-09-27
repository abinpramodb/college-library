package com.library.interfaces;

import com.library.models.BorrowRecord;

public interface FineCalculator {
    double calculateFine(BorrowRecord record, double dailyRate);
}
