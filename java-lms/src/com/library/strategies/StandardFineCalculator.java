package com.library.strategies;

import com.library.interfaces.FineCalculator;
import com.library.models.BorrowRecord;
import java.time.LocalDate;

public class StandardFineCalculator implements FineCalculator {
    @Override
    public double calculateFine(BorrowRecord record, double dailyRate) {
        if (record == null) return 0.0;
        LocalDate compareDate = record.isReturned() ? record.getReturnDate() : LocalDate.now();
        if (compareDate == null) compareDate = LocalDate.now();

        long daysOverdue = record.getDaysOverdue(compareDate);
        if (daysOverdue <= 0) return 0.0;

        return daysOverdue * dailyRate;
    }
}
