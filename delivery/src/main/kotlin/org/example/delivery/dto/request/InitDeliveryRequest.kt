package org.example.delivery.dto.request

data class InitDeliveryRequest(
    val orderId: Long,
    val shippingAddress: String?,
    val billingAddress: String?,
    val type: String = "car"
)

