package org.example.payment.dao

import org.example.payment.jooq.Tables.PAYMENT_
import org.example.payment.jooq.tables.daos.PaymentDao
import org.example.payment.jooq.tables.pojos.Payment
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PaymentDao(
    private val dsl: DSLContext
) : PaymentDao(dsl.configuration()) {

    fun createPayment(orderId: Long, paymentMethod: String): Payment {
        val now = LocalDateTime.now()

        val payment = dsl.insertInto(PAYMENT_)
            .set(PAYMENT_.ORDER_ID, orderId)
            .set(PAYMENT_.IS_PAYED, false)
            .set(PAYMENT_.STATUS, "pending")
            .set(PAYMENT_.PAYMENT_METHOD, paymentMethod)
            .set(PAYMENT_.CREATED_AT, now)
            .set(PAYMENT_.UPDATED_AT, now)
            .returning()
            .fetchInto(Payment::class.java)
            .firstOrNull()!!

        return payment
    }

    fun updatePaymentMethod(paymentId: Long, paymentMethod: String): Payment {
        val now = LocalDateTime.now()

        dsl.update(PAYMENT_)
            .set(PAYMENT_.PAYMENT_METHOD, paymentMethod)
            .set(PAYMENT_.UPDATED_AT, now)
            .where(PAYMENT_.ID.eq(paymentId))
            .execute()

        return fetchById(paymentId).firstOrNull()!!
    }

    fun getByOrderId(orderId: Long): Payment? {
        return dsl.selectFrom(PAYMENT_)
            .where(PAYMENT_.ORDER_ID.eq(orderId))
            .fetchInto(Payment::class.java)
            .firstOrNull()
    }
}

