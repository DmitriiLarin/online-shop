package org.example.auth.controller

import org.example.auth.dto.request.ChangeDataRequest
import org.example.auth.dto.request.ChangePasswordRequest
import org.example.auth.dto.request.LoginRequest
import org.example.auth.dto.request.RegisterRequest
import org.example.auth.dto.response.AuthResponse
import org.example.auth.dto.response.ErrorResponse
import org.example.auth.security.JwtTokenProvider
import org.example.auth.dao.AuthDao
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authDao: AuthDao
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<*> {
        return try {
            // Проверка существования пользователя
            if (authDao.findByUsername(request.username) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorResponse(
                        status = HttpStatus.CONFLICT.value(),
                        error = "Conflict",
                        message = "Username already exists"
                    ))
            }

            if (authDao.findByEmail(request.email) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorResponse(
                        status = HttpStatus.CONFLICT.value(),
                        error = "Conflict",
                        message = "Email already exists"
                    ))
            }

            // Создание нового пользователя
            val encodedPassword = passwordEncoder.encode(request.password)
            val user = org.example.auth.model.User(
                username = request.username,
                password = encodedPassword,
                email = request.email
            )

            val createdUser = authDao.createUser(user)
            val token = jwtTokenProvider.createToken(createdUser.username)

            ResponseEntity.ok(AuthResponse(token = token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = e.message ?: "An error occurred"
                ))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<*> {
        return try {
            val user = authDao.findByUsername(request.username)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = "Invalid username or password"
                    ))

            if (!passwordEncoder.matches(request.password, user.password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = "Invalid username or password"
                    ))
            }

            val token = jwtTokenProvider.createToken(user.username)
            ResponseEntity.ok(AuthResponse(token = token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = e.message ?: "An error occurred"
                ))
        }
    }

    @GetMapping("/logout")
    fun logout(): ResponseEntity<*> {
        // В stateless JWT архитектуре logout обычно обрабатывается на клиенте
        // Здесь просто очищаем контекст безопасности
        SecurityContextHolder.clearContext()
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    @PutMapping("/change/password")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ResponseEntity<*> {
        return try {
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            val username = authentication?.name
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = "User not authenticated"
                    ))

            val user = authDao.findByUsername(username)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Not Found",
                        message = "User not found"
                    ))

            if (!passwordEncoder.matches(request.oldPassword, user.password)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Bad Request",
                        message = "Old password is incorrect"
                    ))
            }

            val encodedNewPassword = passwordEncoder.encode(request.newPassword)
            authDao.updatePassword(user.id!!, encodedNewPassword)

            ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = e.message ?: "An error occurred"
                ))
        }
    }

    @PostMapping("/change/data")
    fun changeData(@RequestBody request: ChangeDataRequest): ResponseEntity<*> {
        return try {
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            val username = authentication?.name
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = "User not authenticated"
                    ))

            val user = authDao.findByUsername(username)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Not Found",
                        message = "User not found"
                    ))

            if (request.email != null) {
                // Проверка, что email не занят другим пользователем
                val existingUser = authDao.findByEmail(request.email)
                if (existingUser != null && existingUser.id != user.id) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ErrorResponse(
                            status = HttpStatus.CONFLICT.value(),
                            error = "Conflict",
                            message = "Email already exists"
                        ))
                }
            }

            authDao.updateUser(user.id!!, request.email)

            ResponseEntity.ok(mapOf("message" to "User data updated successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = e.message ?: "An error occurred"
                ))
        }
    }

    @DeleteMapping("/delete")
    fun delete(): ResponseEntity<*> {
        return try {
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            val username = authentication?.name
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = "User not authenticated"
                    ))

            val user = authDao.findByUsername(username)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Not Found",
                        message = "User not found"
                    ))

            authDao.deleteUser(user.id!!)
            SecurityContextHolder.clearContext()

            ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = e.message ?: "An error occurred"
                ))
        }
    }
}