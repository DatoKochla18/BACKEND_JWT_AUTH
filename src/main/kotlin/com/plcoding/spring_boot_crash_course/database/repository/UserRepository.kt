package com.plcoding.spring_boot_crash_course.database.repository

import com.plcoding.spring_boot_crash_course.database.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
}