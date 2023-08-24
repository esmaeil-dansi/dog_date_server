package com.example.dog_datting.db

import com.example.dog_datting.models.MessageType
import javax.persistence.*


@Entity(name = "messages")
data class Message(
    @GeneratedValue
    @Id
    var id: Int = 0,
    var messageId: Int = 0,
    var from: String = "",
    var to: String = "",
    var type: MessageType = MessageType.TEXT,
    var packetId: String = "",
    var body: String = "",
    var time: Long = 0,
    var isSeen: Boolean = false,
    @ManyToOne val chat: Chat,
)
