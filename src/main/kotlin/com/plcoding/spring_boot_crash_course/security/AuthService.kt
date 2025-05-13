package com.plcoding.spring_boot_crash_course.security

import com.plcoding.spring_boot_crash_course.database.model.RefreshToken
import com.plcoding.spring_boot_crash_course.database.model.User
import com.plcoding.spring_boot_crash_course.database.repository.RefreshTokenRepository
import com.plcoding.spring_boot_crash_course.database.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        userRepository.findByEmail(email.trim())?.let {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")
        }
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("Invalid credentials.")
        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val userId = user.id.toString()
        val access = jwtService.generateAccessToken(userId)
        val refresh = jwtService.generateRefreshToken(userId)
        storeRefreshToken(user.id, refresh)

        return TokenPair(access, refresh)
    }

    @Transactional
    fun logout(userId: UUID, refreshToken: String): Boolean {
        val hashedToken = hashToken(refreshToken)
        val deletedCount = refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashedToken)
        return deletedCount > 0
    }

    @Transactional
    fun refresh(rawToken: String): TokenPair {
        if (!jwtService.validateRefreshToken(rawToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token.")
        }

        val userId = UUID.fromString(jwtService.getUserIdFromToken(rawToken))
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token.")
        }

        val hashed = hashToken(rawToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Refresh token not recognized (maybe used or expired?)"
            )

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccess = jwtService.generateAccessToken(userId.toString())
        val newRefresh = jwtService.generateRefreshToken(userId.toString())
        storeRefreshToken(user.id, newRefresh)

        return TokenPair(newAccess, newRefresh)
    }

    private fun storeRefreshToken(userId: UUID, rawToken: String) {
        val hashed = hashToken(rawToken)
        val expiresAt = Instant.now().plusMillis(jwtService.refreshTokenValidityMs)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                hashedToken = hashed,
                expiresAt = expiresAt
            )
        )
    }

    private fun hashToken(token: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(token.toByteArray())
        return Base64.getEncoder().encodeToString(bytes)
    }
}
