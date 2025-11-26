package org.example.reviews.controller

import org.example.auth.jooq.tables.pojos.Review
import org.example.reviews.client.AuthClient
import org.example.reviews.dto.request.AddReviewRequest
import org.example.reviews.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews")
class ReviewsController(
    private val reviewService: ReviewService,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @GetMapping("/{product_id}/average")
    fun getAverageReviews(@PathVariable("product_id") productId: Long): ResponseEntity<Double> {
        val average = reviewService.getAverageReviews(productId)
        return ResponseEntity.ok(average)
    }

    @DeleteMapping("/{id}/delete")
    fun deleteReviews(
        @RequestHeader(TOKEN) token: String,
        @PathVariable("id") reviewId: Long
    ): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        reviewService.deleteReview(reviewId, user)
        return ResponseEntity.ok("Review deleted")
    }

    @PostMapping("/add")
    fun addReviews(
        @RequestHeader(TOKEN) token: String,
        @RequestBody review: AddReviewRequest
    ): ResponseEntity<Review> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        val createdReview = reviewService.addReview(review, user)
        return ResponseEntity.ok(createdReview)
    }
}