package org.example.reviews.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.reviews.client.AuthClient
import org.example.reviews.dto.request.AddReviewRequest
import org.example.reviews.dto.response.UserDataResponse
import org.example.reviews.jooq.tables.pojos.Review
import org.example.reviews.service.ReviewService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ReviewsControllerTest {

    private lateinit var reviewService: ReviewService
    private lateinit var authClient: AuthClient
    private lateinit var reviewsController: ReviewsController

    @BeforeEach
    fun setUp() {
        reviewService = mockk()
        authClient = mockk()
        reviewsController = ReviewsController(reviewService, authClient)
    }

    @Test
    fun `addReviews should return review when added successfully`() {
        val token = "Bearer test-token"
        val request = AddReviewRequest(
            productId = 1L,
            text = "Great product!",
            estimation = 5
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val review = Review(
            1L,
            request.productId,
            request.text,
            request.estimation
        )

        every { authClient.getUserByToken(token) } returns user
        every { reviewService.addReview(request, user) } returns review

        val response = reviewsController.addReviews(token, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(review, response.body)
        verify { authClient.getUserByToken(token) }
        verify { reviewService.addReview(request, user) }
    }

    @Test
    fun `getAverageReviews should return average estimation`() {
        val productId = 1L
        val average = 4.5

        every { reviewService.getAverageReviews(productId) } returns average

        val response = reviewsController.getAverageReviews(productId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(average, response.body)
        verify { reviewService.getAverageReviews(productId) }
    }

    @Test
    fun `deleteReviews should return success message`() {
        val token = "Bearer test-token"
        val reviewId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { reviewService.deleteReview(reviewId, user) } returns Unit

        val response = reviewsController.deleteReviews(token, reviewId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Review deleted", response.body)
        verify { authClient.getUserByToken(token) }
        verify { reviewService.deleteReview(reviewId, user) }
    }
}

