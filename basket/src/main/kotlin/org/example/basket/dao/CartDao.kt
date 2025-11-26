package org.example.basket.dao

import org.example.auth.jooq.Tables.CART
import org.example.auth.jooq.tables.daos.CartDao
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CartDao(
    private val dsl: DSLContext
) : CartDao(dsl.configuration()) {

    fun createCart(userId: Long): Long {
        val now = LocalDateTime.now()

        val record = dsl.insertInto(CART)
            .set(CART.USER_ID, userId)
            .set(CART.CREATED_AT, now)
            .set(CART.UPDATED_AT, now)
            .returning()
            .fetchOne()

        return record!!.id
    }
}