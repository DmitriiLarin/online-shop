package org.example.payment.dao

import org.example.auth.jooq.Tables.PAYMENT
import org.example.auth.jooq.tables.daos.PaymentDao
import org.example.auth.jooq.tables.pojos.Payment
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PaymentDao(
    private val dsl: DSLContext
) : PaymentDao(dsl.configuration()) {

    fun createPayment(orderId: Long, paymentMethod: String): Payment {
        val now = LocalDateTime.now()

        val payment = dsl.insertInto(PAYMENT)
            .set(PAYMENT.ORDER_ID, orderId)
            .set(PAYMENT.IS_PAYED, false)
            .set(PAYMENT.STATUS, "pending")
            .set(PAYMENT.PAYMENT_METHOD, paymentMethod)
            .set(PAYMENT.CREATED_AT, now)
            .set(PAYMENT.UPDATED_AT, now)
            .returning()
            .fetchInto(Payment::class.java)
            .firstOrNull()!!

        return payment
    }

    fun updatePaymentMethod(paymentId: Long, paymentMethod: String): Payment {
        val now = LocalDateTime.now()

        dsl.update(PAYMENT)
            .set(PAYMENT.PAYMENT_METHOD, paymentMethod)
            .set(PAYMENT.UPDATED_AT, now)
            .where(PAYMENT.ID.eq(paymentId))
            .execute()

        return fetchById(paymentId).firstOrNull()!!
    }

    fun getByOrderId(orderId: Long): Payment? {
        return dsl.selectFrom(PAYMENT)
            .where(PAYMENT.ORDER_ID.eq(orderId))
            .fetchInto(Payment::class.java)
            .firstOrNull()
    }
}

