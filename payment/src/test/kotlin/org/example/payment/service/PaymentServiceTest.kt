package org.example.payment.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.payment.dao.PaymentDao
import org.example.payment.dto.request.InitPaymentRequest
import org.example.payment.dto.response.UserDataResponse
import org.example.payment.jooq.tables.pojos.Payment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class PaymentServiceTest {

    private lateinit var paymentDao: PaymentDao
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun setUp() {
        paymentDao = mockk()
        paymentService = PaymentService(paymentDao)
    }

    @Test
    fun `initPayment should create payment when order has no existing payment`() {
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
            1L,
            request.orderId,
            false,
            "pending",
            request.paymentMethod,
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { paymentDao.getByOrderId(request.orderId) } returns null
        every { paymentDao.createPayment(request.orderId, request.paymentMethod) } returns payment

        val result = paymentService.initPayment(request, user)

        assertNotNull(result)
        assertEquals(payment.id, result.id)
        verify { paymentDao.getByOrderId(request.orderId) }
        verify { paymentDao.createPayment(request.orderId, request.paymentMethod) }
    }

    @Test
    fun `initPayment should throw exception when payment already exists`() {
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
        val existingPayment = Payment(
            1L,
            request.orderId,
            false,
            "pending",
            "card",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { paymentDao.getByOrderId(request.orderId) } returns existingPayment

        assertThrows(ResponseStatusException::class.java) {
            paymentService.initPayment(request, user)
        }
        verify { paymentDao.getByOrderId(request.orderId) }
        verify(exactly = 0) { paymentDao.createPayment(any(), any()) }
    }

    @Test
    fun `changePaymentMethod should throw exception when payment not found`() {
        val paymentId = 1L
        val paymentMethod = "paypal"
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { paymentDao.fetchById(paymentId) } returns emptyList()

        assertThrows(ResponseStatusException::class.java) {
            paymentService.changePaymentMethod(paymentId, paymentMethod, user)
        }
        verify { paymentDao.fetchById(paymentId) }
        verify(exactly = 0) { paymentDao.updatePaymentMethod(any(), any()) }
    }

    @Test
    fun `changePaymentMethod should throw exception when payment is already paid`() {
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
            paymentId,
            1L,
            true,
            "completed",
            "card",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { paymentDao.fetchById(paymentId) } returns listOf(payment)

        assertThrows(ResponseStatusException::class.java) {
            paymentService.changePaymentMethod(paymentId, paymentMethod, user)
        }
        verify { paymentDao.fetchById(paymentId) }
        verify(exactly = 0) { paymentDao.updatePaymentMethod(any(), any()) }
    }
}

