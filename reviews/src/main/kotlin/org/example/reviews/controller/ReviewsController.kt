package org.example.reviews.controller

import org.example.reviews.dto.request.AddReviewRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews")
class ReviewsController(

) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @GetMapping("/{product_id}/average")
    fun getAverageReviews(@PathVariable("product_id") productId: String): ResponseEntity<Double> {

    }

    @GetMapping("/{id}/delete")
    fun deleteReviews(@PathVariable("id") productId: String): ResponseEntity<Double> {

    }

    @PostMapping("/add")
    fun addReviews(@RequestBody review: AddReviewRequest): {
    }


}