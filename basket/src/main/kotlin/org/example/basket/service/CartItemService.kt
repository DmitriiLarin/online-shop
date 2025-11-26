package org.example.basket.service

import org.example.auth.jooq.tables.pojos.Cart
import org.example.auth.jooq.tables.pojos.CartItems
import org.example.auth.jooq.tables.pojos.Orders
import org.example.basket.dao.CartDao
import org.example.basket.dao.CartItemDao
import org.example.basket.dao.OrderDao
import org.example.basket.dao.OrderItemDao
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import org.example.basket.dto.response.UserDataResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class CartItemService(
    private val cartItemDao: CartItemDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    fun addItem(item: ItemAddRequest, user: UserDataResponse): Cart {
        val cart = cartDao.fetchByUserId(user.id).firstOrNull()
        var cartId = cart?.id
        if (cart == null) {
            cartId = cartDao.createCart(user.id)
        }
        else if (cart.userId != user.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val items = cartItemDao.getByCartAndProduct(
            cartId = cartId!!,
            productId = item.productId
        ).firstOrNull()

        if (items == null) {
            cartItemDao.createCartItem(cartId, item.productId, item.quantity)
        } else {
            items.quantity += item.quantity
            items.addedAt = LocalDateTime.now()
            cartItemDao.insert(items)
        }

        return cartDao.fetchByUserId(cartId).firstOrNull()!!
    }

    fun clearCart(user: UserDataResponse){
        val cart = cartDao.fetchByUserId(user.id).firstOrNull() ?: return

        if (cart.userId != user.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        cartItemDao.deleteByCartId(cart.id)
    }

    fun deleteItem(user: UserDataResponse, productId: Long){
        val cart = cartDao.fetchByUserId(user.id).firstOrNull() ?: return

        if (cart.userId != user.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        cartItemDao.deleteByProductId(productId)
    }


    fun createOrder(user: UserDataResponse, cartId: Long, request: CreateOrderRequest): Orders {
        val cart = cartDao.fetchById(cartId).firstOrNull()!!

        if (cart.userId != user.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val items = cartItemDao.fetchByCartId(cartId)

        val order = orderDao.createOrder(
            cart = cart,
            amount = getTotalPrice(items),
            request = request,
        )

        orderItemDao.createItems(order, items)

        cartDao.deleteById(cart.id)
        cartItemDao.deleteByCartId(cart.id)

        return order
    }

    private fun getTotalPrice(items: List<CartItems>): Int {
        var totalPrice = 0
        for (item in items) {
            totalPrice += item.quantity * 100 // лень тащить сюда цену
        }

        return totalPrice
    }
}