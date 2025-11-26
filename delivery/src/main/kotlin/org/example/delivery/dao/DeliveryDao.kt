package org.example.delivery.dao

import org.example.delivery.jooq.tables.daos.DeliveryDao
import org.example.delivery.jooq.tables.pojos.Delivery
import org.example.delivery.jooq.tables.Delivery.DELIVERY_
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class DeliveryDao(
    private val dsl: DSLContext
) : DeliveryDao(dsl.configuration()) {

    fun createDelivery(
        orderId: Long,
        shippingAddress: String?,
        billingAddress: String?,
        type: String
    ): Delivery {
        val now = LocalDateTime.now()

        val delivery = dsl.insertInto(DELIVERY_)
            .set(DELIVERY_.ORDER_ID, orderId)
            .set(DELIVERY_.STATUS, "pending")
            .set(DELIVERY_.SHIPPING_ADDRESS, shippingAddress)
            .set(DELIVERY_.BILLING_ADDRESS, billingAddress)
            .set(DELIVERY_.TYPE, type)
            .set(DELIVERY_.CREATED_AT, now)
            .set(DELIVERY_.UPDATED_AT, now)
            .returning()
            .fetchInto(Delivery::class.java)
            .firstOrNull()!!

        return delivery
    }

    fun updateAddress(
        deliveryId: Long,
        shippingAddress: String?,
        billingAddress: String?
    ): Delivery {
        val now = LocalDateTime.now()

        dsl.update(DELIVERY_)
            .set(DELIVERY_.SHIPPING_ADDRESS, shippingAddress)
            .set(DELIVERY_.BILLING_ADDRESS, billingAddress)
            .set(DELIVERY_.UPDATED_AT, now)
            .where(DELIVERY_.ID.eq(deliveryId))
            .execute()

        return fetchById(deliveryId).firstOrNull()!!
    }

    fun getByOrderId(orderId: Long): Delivery? {
        return dsl.selectFrom(DELIVERY_)
            .where(DELIVERY_.ORDER_ID.eq(orderId))
            .fetchInto(Delivery::class.java)
            .firstOrNull()
    }
}

