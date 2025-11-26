package org.example.reviews.dao

import org.example.auth.jooq.Tables.REVIEW
import org.example.auth.jooq.tables.daos.ReviewDao
import org.example.auth.jooq.tables.pojos.Review
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ReviewDao(
    private val dsl: DSLContext
) : ReviewDao(dsl.configuration()) {

    fun getByProductId(productId: Long): List<Review> {
        return dsl.selectFrom(REVIEW)
            .where(REVIEW.PRODUCT_ID.eq(productId))
            .fetchInto(Review::class.java)
    }

    fun getAverageEstimationByProductId(productId: Long): Double? {
        val result = dsl.select(org.jooq.impl.DSL.avg(REVIEW.ESTIMATION))
            .from(REVIEW)
            .where(REVIEW.PRODUCT_ID.eq(productId))
            .fetchOne()

        return result?.value1()?.toDouble()
    }

    fun createReview(productId: Long, text: String?, estimation: Int): Review {
        val review = dsl.insertInto(REVIEW)
            .set(REVIEW.PRODUCT_ID, productId)
            .set(REVIEW.TEXT, text)
            .set(REVIEW.ESTIMATION, estimation)
            .returning()
            .fetchInto(Review::class.java)
            .firstOrNull()!!

        return review
    }

    fun deleteById(reviewId: Long) {
        dsl.deleteFrom(REVIEW)
            .where(REVIEW.ID.eq(reviewId))
            .execute()
    }
}

