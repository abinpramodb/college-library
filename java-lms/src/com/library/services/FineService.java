package com.library.services;

import com.library.enums.PaymentMethod;
import com.library.enums.PaymentStatus;
import com.library.interfaces.FineCalculator;
import com.library.interfaces.PaymentProcessor;
import com.library.models.Fine;
import com.library.models.Student;
import com.library.strategies.CashPaymentProcessor;
import com.library.strategies.OnlinePaymentProcessor;
import com.library.strategies.StandardFineCalculator;
import com.library.strategies.UpiPaymentProcessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FineService {
    private final FineCalculator fineCalculator;
    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors = new HashMap<>();
    private final Map<Integer, Fine> finesById = new ConcurrentHashMap<>();
    private final AtomicInteger nextFineId = new AtomicInteger(1001);
    private double totalFinesCollected = 840.0; // Seeded today amount

    public FineService() {
        this.fineCalculator = new StandardFineCalculator();
        registerProcessor(new UpiPaymentProcessor());
        registerProcessor(new CashPaymentProcessor());
        registerProcessor(new OnlinePaymentProcessor());
        seedInitialFines();
    }

    public void registerProcessor(PaymentProcessor processor) {
        paymentProcessors.put(processor.getPaymentMethod(), processor);
    }

    public Fine createFine(String studentId, int recordId, String bookTitle, double amount) {
        int id = nextFineId.getAndIncrement();
        Fine fine = new Fine(id, studentId, recordId, bookTitle, amount);
        finesById.put(id, fine);
        return fine;
    }

    public List<Fine> getFinesForStudent(String studentId) {
        return finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId))
                .sorted(Comparator.comparing(Fine::getFineId).reversed())
                .collect(Collectors.toList());
    }

    public double getPendingFineTotalForStudent(String studentId) {
        return finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId) && f.getStatus() == PaymentStatus.PENDING)
                .mapToDouble(Fine::getAmount)
                .sum();
    }

    public synchronized boolean payFine(int fineId, PaymentMethod method, String transactionRef, Student student) {
        Fine fine = finesById.get(fineId);
        if (fine == null || fine.getStatus() == PaymentStatus.PAID) {
            return false;
        }

        PaymentProcessor processor = paymentProcessors.getOrDefault(method, new UpiPaymentProcessor());
        boolean success = processor.processPayment(fine.getStudentId(), fine.getAmount(), transactionRef);

        if (success) {
            fine.settle(method, transactionRef);
            totalFinesCollected += fine.getAmount();
            if (student != null) {
                student.deductFine(fine.getAmount());
            }
            return true;
        }
        return false;
    }

    public synchronized boolean payAllFinesForStudent(String studentId, PaymentMethod method, String transactionRef, Student student) {
        List<Fine> pending = finesById.values().stream()
                .filter(f -> f.getStudentId().equalsIgnoreCase(studentId) && f.getStatus() == PaymentStatus.PENDING)
                .collect(Collectors.toList());

        if (pending.isEmpty()) return false;

        for (Fine f : pending) {
            f.settle(method, transactionRef);
            totalFinesCollected += f.getAmount();
        }
        if (student != null) {
            student.deductFine(student.getOutstandingFine());
        }
        return true;
    }

    public double getTotalFinesCollectedToday() {
        return totalFinesCollected;
    }

    public FineCalculator getFineCalculator() {
        return fineCalculator;
    }

    private void seedInitialFines() {
        Fine f1 = createFine("2026CS142", 1, "Database System Concepts", 40.0);
        // Previously paid fine history
        Fine f2 = createFine("2026CS142", 2, "Operating System Concepts", 20.0);
        f2.settle(PaymentMethod.UPI, "TXN-842918");

        Fine f3 = createFine("2026CS108", 3, "Computer Networks", 15.0);
        f3.settle(PaymentMethod.CASH, "CASH-REC-102");
    }
}
