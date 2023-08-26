package com.example.dog_datting.repo

import com.example.dog_datting.db.Chat
import com.example.dog_datting.db.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepo : JpaRepository<Message, Long> {
    fun getMessageByMessageIdAndFrom(messageId: Int, from: String): Message?

    fun getMessageByChat(chat: Chat): List<Message>?
}