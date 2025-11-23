package org.example.auth.controller

import org.example.serverforandroid.dto.request.LoginRequest
import org.example.serverforandroid.dto.request.RegisterRequest
import org.example.serverforandroid.dto.response.AuthResponse
import org.example.serverforandroid.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping


@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): AuthResponse {

    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse {

    }

    @GetMapping("/logout")
    fun logout(@RequestBody request: LoginRequest): AuthResponse {

    }

    @PutMapping("/change/password")
    fun ChangePassword(@RequestBody request: LoginRequest): AuthResponse {

    }

    @PostMapping("/change/data")
    fun ChangeData(@RequestBody request: LoginRequest): AuthResponse {

    }

    @DeleteMapping("/delete")
    fun delete(@RequestBody request: LoginRequest): AuthResponse {

    }

}