package org.example.basket.dao

import org.example.basket.jooq.Tables.CART_ITEMS
import org.example.basket.jooq.tables.daos.CartItemsDao
import org.example.basket.jooq.tables.pojos.CartItems
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CartItemDao(
    private val dsl: DSLContext
) : CartItemsDao(dsl.configuration()) {

    fun getByCartAndProduct(cartId: Long, productId: Long): List<CartItems> {
        return dsl.selectFrom(CART_ITEMS)
            .where(CART_ITEMS.PRODUCT_ID.eq(productId))
            .and(CART_ITEMS.CART_ID.eq(cartId))
            .fetchInto(CartItems::class.java)
    }

    fun createCartItem(cartId: Long, productId: Long, quantity: Int) {
        dsl.insertInto(CART_ITEMS)
            .set(CART_ITEMS.PRODUCT_ID, productId)
            .set(CART_ITEMS.CART_ID, cartId)
            .set(CART_ITEMS.QUANTITY, quantity)
            .set(CART_ITEMS.ADDED_AT, LocalDateTime.now())
            .execute()
    }

    fun deleteByCartId(cartId: Long) {
        dsl.deleteFrom(CART_ITEMS)
            .where(CART_ITEMS.CART_ID.eq(cartId))
    }

    fun deleteByProductId(productId: Long) {
        dsl.deleteFrom(CART_ITEMS)
            .where(CART_ITEMS.PRODUCT_ID.eq(productId))
    }
}