package org.example.payment.controller

import org.example.payment.client.AuthClient
import org.example.payment.dto.request.InitPaymentRequest
import org.example.payment.dto.response.InitPaymentReponse
import org.example.payment.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(
    private val paymentService: PaymentService,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/init")
    fun initPayment(
        @RequestHeader(TOKEN) token: String,
        @RequestBody request: InitPaymentRequest
    ): ResponseEntity<InitPaymentReponse> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        val payment = paymentService.initPayment(request, user)

        val response = InitPaymentReponse(
            id = payment.id,
            priceToPay = 0L // Нужно получить из заказа
        )
        
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/change-method")
    fun changePaymentMethod(
        @RequestHeader(TOKEN) token: String,
        @PathVariable("id") paymentId: Long,
        @RequestBody paymentMethod: String
    ): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        paymentService.changePaymentMethod(paymentId, paymentMethod, user)
        return ResponseEntity.ok("Payment method changed")
    }
}