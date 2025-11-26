package org.example.delivery.dao

import org.example.auth.jooq.Tables.DELIVERY
import org.example.auth.jooq.tables.daos.DeliveryDao
import org.example.auth.jooq.tables.pojos.Delivery
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

        val delivery = dsl.insertInto(DELIVERY)
            .set(DELIVERY.ORDER_ID, orderId)
            .set(DELIVERY.STATUS, "pending")
            .set(DELIVERY.SHIPPING_ADDRESS, shippingAddress)
            .set(DELIVERY.BILLING_ADDRESS, billingAddress)
            .set(DELIVERY.TYPE, type)
            .set(DELIVERY.CREATED_AT, now)
            .set(DELIVERY.UPDATED_AT, now)
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

        dsl.update(DELIVERY)
            .set(DELIVERY.SHIPPING_ADDRESS, shippingAddress)
            .set(DELIVERY.BILLING_ADDRESS, billingAddress)
            .set(DELIVERY.UPDATED_AT, now)
            .where(DELIVERY.ID.eq(deliveryId))
            .execute()

        return fetchById(deliveryId).firstOrNull()!!
    }

    fun getByOrderId(orderId: Long): Delivery? {
        return dsl.selectFrom(DELIVERY)
            .where(DELIVERY.ORDER_ID.eq(orderId))
            .fetchInto(Delivery::class.java)
            .firstOrNull()
    }
}

