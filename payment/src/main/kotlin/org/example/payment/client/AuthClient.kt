package org.example.payment.client

import org.example.payment.config.FeignConfig
import org.example.payment.dto.response.UserDataResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader


@FeignClient(name = "auth", url = "http://localhost:8080/api/v1/auth", configuration = [FeignConfig::class])
interface AuthClient {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @GetMapping("/get-by-token")
    fun getUserByToken(@RequestHeader(TOKEN) token: String?): UserDataResponse?
}