package com.example.dog_datting.repo

import com.example.dog_datting.db.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepo : JpaRepository<Chat, String> {
    fun getByOwnerId(ownerId: String): List<Chat>?
    fun getByOwnerIdAndUserId(ownerId: String, userId: String): Chat?
}