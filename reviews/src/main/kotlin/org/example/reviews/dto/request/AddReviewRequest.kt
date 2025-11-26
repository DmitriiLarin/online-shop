package org.example.reviews.dto.request

data class AddReviewRequest(
    val productId: Long,
    val text: String,
    val estimation: Int
)
