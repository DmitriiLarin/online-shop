package org.example.basket.dao

import org.example.auth.jooq.tables.Orders.ORDERS
import org.example.auth.jooq.tables.daos.OrdersDao
import org.example.auth.jooq.tables.pojos.Cart
import org.example.auth.jooq.tables.pojos.Orders
import org.example.basket.dto.request.CreateOrderRequest
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OrderDao(
    private val dsl: DSLContext
) : OrdersDao(dsl.configuration()) {

    fun createOrder(cart: Cart, amount: Int, request: CreateOrderRequest): Orders {
        val now = LocalDateTime.now()

        val order = dsl.insertInto(ORDERS)
            .set(ORDERS.USER_ID, cart.userId)
            .set(ORDERS.TOTAL_AMOUNT, amount)
            .set(ORDERS.ORDER_NUMBER, request.orderNumber)
            .set(ORDERS.SHIPPING_ADDRESS, request.shippingAddress)
            .set(ORDERS.BILLING_ADDRESS, request.billingAddress)
            .set(ORDERS.CREATED_AT, now)
            .set(ORDERS.UPDATED_AT, now)
            .returning()
            .fetchInto(Orders::class.java).firstOrNull()!!

        return order
    }

}