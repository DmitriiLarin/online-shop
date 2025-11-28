package org.example.delivery.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.delivery.client.AuthClient
import org.example.delivery.dto.request.ChangeAddressRequest
import org.example.delivery.dto.request.InitDeliveryRequest
import org.example.delivery.dto.response.UserDataResponse
import org.example.delivery.jooq.tables.pojos.Delivery
import org.example.delivery.service.DeliveryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class DeliveryControllerTest {

    private lateinit var deliveryService: DeliveryService
    private lateinit var authClient: AuthClient
    private lateinit var deliveryController: DeliveryController

    @BeforeEach
    fun setUp() {
        deliveryService = mockk()
        authClient = mockk()
        deliveryController = DeliveryController(deliveryService, authClient)
    }

    @Test
    fun `initDelivery should return delivery when created successfully`() {
        val token = "Bearer test-token"
        val request = InitDeliveryRequest(
            orderId = 1L,
            shippingAddress = "123 Main St",
            billingAddress = "123 Main St",
            type = "car"
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val delivery = Delivery(
            id = 1L,
            orderId = request.orderId,
            status = "pending",
            shippingAddress = request.shippingAddress,
            billingAddress = request.billingAddress,
            type = request.type,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { deliveryService.initDelivery(request, user) } returns delivery

        val response = deliveryController.initDelivery(token, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(delivery, response.body)
        verify { authClient.getUserByToken(token) }
        verify { deliveryService.initDelivery(request, user) }
    }

    @Test
    fun `changeAddress should return updated delivery`() {
        val token = "Bearer test-token"
        val deliveryId = 1L
        val request = ChangeAddressRequest(
            shippingAddress = "456 New St",
            billingAddress = "456 New St"
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val delivery = Delivery(
            id = deliveryId,
            orderId = 1L,
            status = "pending",
            shippingAddress = request.shippingAddress,
            billingAddress = request.billingAddress,
            type = "car",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { deliveryService.changeAddress(deliveryId, request, user) } returns delivery

        val response = deliveryController.changeAddress(token, deliveryId, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(delivery, response.body)
        verify { authClient.getUserByToken(token) }
        verify { deliveryService.changeAddress(deliveryId, request, user) }
    }
}

