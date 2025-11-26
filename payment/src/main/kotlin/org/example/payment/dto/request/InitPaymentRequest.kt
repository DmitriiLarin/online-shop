package org.example.payment.dto.request

data class InitPaymentRequest(
    val orderId: Long,
    val paymentMethod: String,
)
