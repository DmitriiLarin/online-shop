package org.example.reviews.service

import org.example.auth.jooq.tables.pojos.Review
import org.example.reviews.dao.ReviewDao
import org.example.reviews.dto.request.AddReviewRequest
import org.example.reviews.dto.response.UserDataResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ReviewService(
    private val reviewDao: ReviewDao
) {

    fun addReview(request: AddReviewRequest, user: UserDataResponse): Review {
        if (request.estimation < 0 || request.estimation > 5) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Estimation must be between 0 and 5")
        }

        return reviewDao.createReview(
            productId = request.productId,
            text = request.text,
            estimation = request.estimation
        )
    }

    fun getAverageReviews(productId: Long): Double {
        return reviewDao.getAverageEstimationByProductId(productId) ?: 0.0
    }

    fun deleteReview(reviewId: Long, user: UserDataResponse) {
        val review = reviewDao.fetchById(reviewId).firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found")

        reviewDao.deleteById(reviewId)
    }
}

