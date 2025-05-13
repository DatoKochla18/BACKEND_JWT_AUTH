package com.plcoding.spring_boot_crash_course.database.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "uniqueidentifier", nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val hashedToken: String,

    @Column(nullable = false)
    val expiresAt: Instant,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
) {

}