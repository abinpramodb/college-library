package com.library.interfaces;

import com.library.enums.PaymentMethod;

public interface PaymentProcessor {
    PaymentMethod getPaymentMethod();
    boolean processPayment(String studentId, double amount, String reference);
}
