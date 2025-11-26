package org.example.basket.dao

import org.example.auth.jooq.tables.OrderItems.ORDER_ITEMS
import org.example.auth.jooq.tables.daos.OrderItemsDao
import org.example.auth.jooq.tables.pojos.CartItems
import org.example.auth.jooq.tables.pojos.Orders
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OrderItemDao(
    private val dsl: DSLContext
) : OrderItemsDao(dsl.configuration()) {

    fun createItems(order: Orders, items: List<CartItems>) {
        items.forEach {
            dsl.insertInto(ORDER_ITEMS)
                .set(ORDER_ITEMS.ORDER_ID, order.id)
                .set(ORDER_ITEMS.PRODUCT_ID, it.productId)
                .set(ORDER_ITEMS.QUANTITY, it.quantity)
                .set(ORDER_ITEMS.UNIT_PRICE, 100)
                .set(ORDER_ITEMS.TOTAL_PRICE, it.quantity * 100)
                .set(ORDER_ITEMS.CREATED_AT, LocalDateTime.now())
        }
    }


}