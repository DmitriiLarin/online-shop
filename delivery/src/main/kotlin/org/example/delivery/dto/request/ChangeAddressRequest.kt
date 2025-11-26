package org.example.delivery.dto.request

data class ChangeAddressRequest(
    val shippingAddress: String?,
    val billingAddress: String?
)

