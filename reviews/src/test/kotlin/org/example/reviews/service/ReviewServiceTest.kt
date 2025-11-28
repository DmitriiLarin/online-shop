package org.example.reviews.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.reviews.dao.ReviewDao
import org.example.reviews.dto.request.AddReviewRequest
import org.example.reviews.dto.response.UserDataResponse
import org.example.reviews.jooq.tables.pojos.Review
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class ReviewServiceTest {

    private lateinit var reviewDao: ReviewDao
    private lateinit var reviewService: ReviewService

    @BeforeEach
    fun setUp() {
        reviewDao = mockk()
        reviewService = ReviewService(reviewDao)
    }

    @Test
    fun `addReview should create review when estimation is valid`() {
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
            id = 1L,
            productId = request.productId,
            text = request.text,
            estimation = request.estimation
        )

        every { reviewDao.createReview(request.productId, request.text, request.estimation) } returns review

        val result = reviewService.addReview(request, user)

        assertNotNull(result)
        assertEquals(review.id, result.id)
        verify { reviewDao.createReview(request.productId, request.text, request.estimation) }
    }

    @Test
    fun `addReview should throw exception when estimation is invalid`() {
        val request = AddReviewRequest(
            productId = 1L,
            text = "Bad product!",
            estimation = 10
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        assertThrows(ResponseStatusException::class.java) {
            reviewService.addReview(request, user)
        }
        verify(exactly = 0) { reviewDao.createReview(any(), any(), any()) }
    }

    @Test
    fun `getAverageReviews should return average estimation`() {
        val productId = 1L
        val average = 4.5

        every { reviewDao.getAverageEstimationByProductId(productId) } returns average

        val result = reviewService.getAverageReviews(productId)

        assertEquals(average, result)
        verify { reviewDao.getAverageEstimationByProductId(productId) }
    }

    @Test
    fun `getAverageReviews should return zero when no reviews exist`() {
        val productId = 1L

        every { reviewDao.getAverageEstimationByProductId(productId) } returns null

        val result = reviewService.getAverageReviews(productId)

        assertEquals(0.0, result)
        verify { reviewDao.getAverageEstimationByProductId(productId) }
    }

    @Test
    fun `deleteReview should delete review when found`() {
        val reviewId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val review = Review(
            id = reviewId,
            productId = 1L,
            text = "Review text",
            estimation = 5
        )

        every { reviewDao.fetchById(reviewId) } returns listOf(review)
        every { reviewDao.deleteById(reviewId) } returns Unit

        reviewService.deleteReview(reviewId, user)

        verify { reviewDao.fetchById(reviewId) }
        verify { reviewDao.deleteById(reviewId) }
    }

    @Test
    fun `deleteReview should throw exception when review not found`() {
        val reviewId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { reviewDao.fetchById(reviewId) } returns emptyList()

        assertThrows(ResponseStatusException::class.java) {
            reviewService.deleteReview(reviewId, user)
        }
        verify { reviewDao.fetchById(reviewId) }
        verify(exactly = 0) { reviewDao.deleteById(any()) }
    }
}

