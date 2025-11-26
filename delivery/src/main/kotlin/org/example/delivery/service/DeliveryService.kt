package org.example.delivery.service

import org.example.auth.jooq.tables.pojos.Delivery
import org.example.delivery.dao.DeliveryDao
import org.example.delivery.dto.request.ChangeAddressRequest
import org.example.delivery.dto.request.InitDeliveryRequest
import org.example.delivery.dto.response.UserDataResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class DeliveryService(
    private val deliveryDao: DeliveryDao
) {

    fun initDelivery(request: InitDeliveryRequest, user: UserDataResponse): Delivery {
        val existingDelivery = deliveryDao.getByOrderId(request.orderId)
        if (existingDelivery != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery already exists for this order")
        }

        return deliveryDao.createDelivery(
            orderId = request.orderId,
            shippingAddress = request.shippingAddress,
            billingAddress = request.billingAddress,
            type = request.type
        )
    }

    fun changeAddress(
        deliveryId: Long,
        request: ChangeAddressRequest,
        user: UserDataResponse
    ): Delivery {
        val delivery = deliveryDao.fetchById(deliveryId).firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found")

        if (delivery.status == "delivered" || delivery.status == "shipped") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change address for delivery in progress or completed")
        }

        return deliveryDao.updateAddress(
            deliveryId = deliveryId,
            shippingAddress = request.shippingAddress,
            billingAddress = request.billingAddress
        )
    }
}

