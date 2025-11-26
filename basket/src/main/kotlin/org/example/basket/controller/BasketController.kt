package org.example.basket.controller

import org.example.auth.jooq.tables.pojos.Cart
import org.example.auth.jooq.tables.pojos.Orders
import org.example.basket.client.AuthClient
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import org.example.basket.service.CartItemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/basket")
class BasketController(
    private val orderItemService: CartItemService,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/item/add")
    fun addItem(@RequestHeader(TOKEN) token: String, @RequestBody request: ItemAddRequest): ResponseEntity<Cart> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        val cart = orderItemService.addItem(request, user)

        return ResponseEntity.ok(cart)
    }

    @DeleteMapping("/cart/{productId}/delete")
    fun deleteItem(@RequestHeader(TOKEN) token: String, @PathVariable productId: Long): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        orderItemService.deleteItem(user, productId)

        return ResponseEntity.ok("Cart cleared")
    }

    @DeleteMapping("/cart/clear")
    fun cartClear(@RequestHeader(TOKEN) token: String): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        orderItemService.clearCart(user)

        return ResponseEntity.ok("Cart cleared")
    }

    @PostMapping("/cart/{cartId}/order/create")
    fun orderArranges(
        @RequestHeader(TOKEN) token: String,
        @PathVariable cartId: Long,
        @RequestBody request: CreateOrderRequest
    ): ResponseEntity<Orders> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")

        val order = orderItemService.createOrder(user, cartId, request)
        return ResponseEntity.ok(order)
    }
}