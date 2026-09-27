package com.library.strategies;

import com.library.enums.PaymentMethod;
import com.library.interfaces.PaymentProcessor;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Fast UPI QR validation
        return amount > 0;
    }
}
