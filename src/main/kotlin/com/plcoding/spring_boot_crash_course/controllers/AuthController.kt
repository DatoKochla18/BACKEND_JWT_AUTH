package com.plcoding.spring_boot_crash_course.controllers

import com.plcoding.spring_boot_crash_course.security.AuthService
import com.plcoding.spring_boot_crash_course.security.JwtService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtService: JwtService
) {
    data class AuthRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    data class LogoutRequest(
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: AuthRequest
    ) {
        authService.register(body.email, body.password)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.TokenPair {
        return authService.login(body.email, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthService.TokenPair {
        return authService.refresh(body.refreshToken)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: LogoutRequest): ResponseEntity<Void> {
        val rawToken = request.refreshToken
        if (!jwtService.validateRefreshToken(rawToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userId = UUID.fromString(jwtService.getUserIdFromToken(rawToken))
        val deleted = authService.logout(userId, rawToken)

        return if (deleted) ResponseEntity.ok().build()
        else ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }
}