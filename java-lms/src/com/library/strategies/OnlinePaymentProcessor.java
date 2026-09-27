package com.library.strategies;

import com.library.enums.PaymentMethod;
import com.library.interfaces.PaymentProcessor;

public class OnlinePaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NET_BANKING;
    }

    @Override
    public boolean processPayment(String studentId, double amount, String reference) {
        // Online gateway confirmation check
        return amount > 0 && reference != null && !reference.isBlank();
    }
}
