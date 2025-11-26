package org.example.delivery.controller

import org.example.delivery.jooq.tables.pojos.Delivery
import org.example.delivery.client.AuthClient
import org.example.delivery.dto.request.ChangeAddressRequest
import org.example.delivery.dto.request.InitDeliveryRequest
import org.example.delivery.service.DeliveryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/delivery")
class DeliveryController(
    private val deliveryService: DeliveryService,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/init")
    fun initDelivery(
        @RequestHeader(TOKEN) token: String,
        @RequestBody request: InitDeliveryRequest
    ): ResponseEntity<Delivery> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        val delivery = deliveryService.initDelivery(request, user)
        return ResponseEntity.ok(delivery)
    }

    @PutMapping("/{id}/change-address")
    fun changeAddress(
        @RequestHeader(TOKEN) token: String,
        @PathVariable("id") deliveryId: Long,
        @RequestBody request: ChangeAddressRequest
    ): ResponseEntity<Delivery> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        val delivery = deliveryService.changeAddress(deliveryId, request, user)
        return ResponseEntity.ok(delivery)
    }
}