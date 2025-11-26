package org.example.payment.controller

import org.example.payment.dto.request.InitPaymentRequest
import org.example.payment.dto.response.InitPaymentReponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(

) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/init")
    fun initPayment(@RequestBody request: InitPaymentRequest): ResponseEntity<InitPaymentReponse> {

    }

    @PostMapping("/{id}/")
    fun changePaymentMethod(@RequestBody request: String): ResponseEntity<String> {

    }


}