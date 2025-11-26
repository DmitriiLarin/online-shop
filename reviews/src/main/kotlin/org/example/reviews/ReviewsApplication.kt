package org.example.reviews

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["org.example.reviews.client"])
class ReviewsApplication

fun main(args: Array<String>) {
	runApplication<ReviewsApplication>(*args)
}
