package com.library.strategies;

import com.library.enums.PaymentMethod;
import com.library.interfaces.PaymentProcessor;

public class CashPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CASH;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Librarian verified cash receipt
        return amount > 0;
    }
}
