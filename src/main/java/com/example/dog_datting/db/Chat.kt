package com.example.dog_datting.db

import com.example.dog_datting.models.UserId
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Chat(
    @GeneratedValue
    @Id var id: Long = 0,
    var ownerId: String = "",
    var lastMessageId: Int = 0,
    var userId: String = "",
    var lastTime: Long = 0,
    var lastMessage: String = ""


)