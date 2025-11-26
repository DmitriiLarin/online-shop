package org.example.payment.service

import org.example.payment.jooq.tables.pojos.Payment
import org.example.payment.dao.PaymentDao
import org.example.payment.dto.request.InitPaymentRequest
import org.example.payment.dto.response.UserDataResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PaymentService(
    private val paymentDao: PaymentDao
) {

    fun initPayment(request: InitPaymentRequest, user: UserDataResponse): Payment {
        val existingPayment = paymentDao.getByOrderId(request.orderId)
        if (existingPayment != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment already exists for this order")
        }

        return paymentDao.createPayment(
            orderId = request.orderId,
            paymentMethod = request.paymentMethod
        )
    }

    fun changePaymentMethod(paymentId: Long, paymentMethod: String, user: UserDataResponse): Payment {
        val payment = paymentDao.fetchById(paymentId).firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")

        if (payment.isPayed == true) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change payment method for paid payment")
        }

        return paymentDao.updatePaymentMethod(paymentId, paymentMethod)
    }
}

