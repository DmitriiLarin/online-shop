package org.example.delivery.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.delivery.dao.DeliveryDao
import org.example.delivery.dto.request.ChangeAddressRequest
import org.example.delivery.dto.request.InitDeliveryRequest
import org.example.delivery.dto.response.UserDataResponse
import org.example.delivery.jooq.tables.pojos.Delivery
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class DeliveryServiceTest {

    private lateinit var deliveryDao: DeliveryDao
    private lateinit var deliveryService: DeliveryService

    @BeforeEach
    fun setUp() {
        deliveryDao = mockk()
        deliveryService = DeliveryService(deliveryDao)
    }

    @Test
    fun `initDelivery should create delivery when order has no existing delivery`() {
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
            1L,
            request.orderId,
            "pending",
            request.shippingAddress,
            request.billingAddress,
            request.type,
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { deliveryDao.getByOrderId(request.orderId) } returns null
        every {
            deliveryDao.createDelivery(
                request.orderId,
                request.shippingAddress,
                request.billingAddress,
                request.type
            )
        } returns delivery

        val result = deliveryService.initDelivery(request, user)

        assertNotNull(result)
        assertEquals(delivery.id, result.id)
        verify { deliveryDao.getByOrderId(request.orderId) }
        verify {
            deliveryDao.createDelivery(
                request.orderId,
                request.shippingAddress,
                request.billingAddress,
                request.type
            )
        }
    }

    @Test
    fun `initDelivery should throw exception when delivery already exists`() {
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
        val existingDelivery = Delivery(
            1L,
            request.orderId,
            "pending",
            "123 Main St",
            "123 Main St",
            "car",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { deliveryDao.getByOrderId(request.orderId) } returns existingDelivery

        assertThrows(ResponseStatusException::class.java) {
            deliveryService.initDelivery(request, user)
        }
        verify { deliveryDao.getByOrderId(request.orderId) }
        verify(exactly = 0) { deliveryDao.createDelivery(any(), any(), any(), any()) }
    }

    @Test
    fun `changeAddress should throw exception when delivery not found`() {
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

        every { deliveryDao.fetchById(deliveryId) } returns emptyList()

        assertThrows(ResponseStatusException::class.java) {
            deliveryService.changeAddress(deliveryId, request, user)
        }
        verify { deliveryDao.fetchById(deliveryId) }
        verify(exactly = 0) { deliveryDao.updateAddress(any(), any(), any()) }
    }

    @Test
    fun `changeAddress should throw exception when delivery is in progress`() {
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
            deliveryId,
            1L,
            "shipped",
            "123 Main St",
            "123 Main St",
            "car",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { deliveryDao.fetchById(deliveryId) } returns listOf(delivery)

        assertThrows(ResponseStatusException::class.java) {
            deliveryService.changeAddress(deliveryId, request, user)
        }
        verify { deliveryDao.fetchById(deliveryId) }
        verify(exactly = 0) { deliveryDao.updateAddress(any(), any(), any()) }
    }
}

