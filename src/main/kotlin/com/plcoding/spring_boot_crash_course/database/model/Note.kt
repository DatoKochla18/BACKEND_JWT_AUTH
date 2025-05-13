package com.plcoding.spring_boot_crash_course.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "notes")
data class Note(
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "text")
    val content: String,

    @Column(nullable = false)
    val color: Long,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(columnDefinition = "uniqueidentifier", nullable = false)
    val ownerId: UUID
)