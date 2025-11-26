package org.example.delivery.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/delivery")
class DeliveryController(

) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping()
    fun initDelivery(): {

    }

    fun chandeAddress(): {

    }




}