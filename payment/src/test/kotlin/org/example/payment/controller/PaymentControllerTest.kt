package org.example.payment.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.payment.client.AuthClient
import org.example.payment.dto.request.InitPaymentRequest
import org.example.payment.dto.response.InitPaymentReponse
import org.example.payment.dto.response.UserDataResponse
import org.example.payment.jooq.tables.pojos.Payment
import org.example.payment.service.PaymentService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class PaymentControllerTest {

    private lateinit var paymentService: PaymentService
    private lateinit var authClient: AuthClient
    private lateinit var paymentController: PaymentController

    @BeforeEach
    fun setUp() {
        paymentService = mockk()
        authClient = mockk()
        paymentController = PaymentController(paymentService, authClient)
    }

    @Test
    fun `initPayment should return payment response`() {
        val token = "Bearer test-token"
        val request = InitPaymentRequest(
            orderId = 1L,
            paymentMethod = "card"
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val payment = Payment(
            id = 1L,
            orderId = request.orderId,
            isPayed = false,
            status = "pending",
            paymentMethod = request.paymentMethod,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { paymentService.initPayment(request, user) } returns payment

        val response = paymentController.initPayment(token, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(payment.id, response.body?.id)
        verify { authClient.getUserByToken(token) }
        verify { paymentService.initPayment(request, user) }
    }

    @Test
    fun `changePaymentMethod should return success message`() {
        val token = "Bearer test-token"
        val paymentId = 1L
        val paymentMethod = "paypal"
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val payment = Payment(
            id = paymentId,
            orderId = 1L,
            isPayed = false,
            status = "pending",
            paymentMethod = paymentMethod,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { paymentService.changePaymentMethod(paymentId, paymentMethod, user) } returns payment

        val response = paymentController.changePaymentMethod(token, paymentId, paymentMethod)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Payment method changed", response.body)
        verify { authClient.getUserByToken(token) }
        verify { paymentService.changePaymentMethod(paymentId, paymentMethod, user) }
    }
}

