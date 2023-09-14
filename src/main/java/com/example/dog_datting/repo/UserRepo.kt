package com.example.dog_datting.repo

import com.example.dog_datting.db.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo : JpaRepository<User, Int> {
    fun getUserByUuid(uuid: String): User?
    fun getUserByUsername(username: String): User?

    fun getUserByEmail(email: String): User?

    fun getUserByPhoneNumber(phoneNumber: String): User?

    fun findByLocationIsNotNull(): List<User>?

}