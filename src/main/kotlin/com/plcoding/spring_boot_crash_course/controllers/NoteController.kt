package com.plcoding.spring_boot_crash_course.controllers

import com.plcoding.spring_boot_crash_course.database.model.Note
import com.plcoding.spring_boot_crash_course.database.repository.NoteRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

// POST http://localhost:8085/notes
// GET http://localhost:8085/notes?ownerId=123
// DELETE http://localhost:8085/notes/123

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteRepository: NoteRepository
) {
    data class NoteRequest(
        val id: String?,
        @field:NotBlank(message = "Title can't be blank.")
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant
    )

    @PostMapping
    fun save(
        @Valid @RequestBody body: NoteRequest
    ): ResponseEntity<NoteResponse> {
        val ownerIdStr = SecurityContextHolder.getContext().authentication.principal as String
        val ownerId = UUID.fromString(ownerIdStr)

        val noteId = body.id?.let(UUID::fromString) ?: UUID.randomUUID()
        val note = Note(
            id = noteId,
            title = body.title,
            content = body.content,
            color = body.color,
            createdAt = Instant.now(),
            ownerId = ownerId
        )
        val saved = noteRepository.save(note)
        return ResponseEntity.ok(saved.toResponse())
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownerIdStr = SecurityContextHolder.getContext().authentication.principal as String
        val ownerId = UUID.fromString(ownerIdStr)
        return noteRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun findNoteByOwnerId(@PathVariable id: String): NoteResponse {
        val ownerIdStr = SecurityContextHolder.getContext().authentication.principal as String
        val ownerId = UUID.fromString(ownerIdStr)
        return noteRepository.findByOwnerId(ownerId).map { it.toResponse() }.first { it.id == id }
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: String): ResponseEntity<Void> {
        val uuid = UUID.fromString(id)
        val note = noteRepository.findById(uuid).orElseThrow {
            IllegalArgumentException("Note not found")
        }
        val ownerIdStr = SecurityContextHolder.getContext().authentication.principal as String
        if (note.ownerId.toString() == ownerIdStr) {
            noteRepository.deleteById(uuid)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.status(403).build()
    }

    private fun Note.toResponse() = NoteResponse(
        id = id.toString(),
        title = title,
        content = content,
        color = color,
        createdAt = createdAt
    )
}