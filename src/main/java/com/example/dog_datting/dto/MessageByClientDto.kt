package com.example.dog_datting.dto

import com.example.dog_datting.models.Location


data class MessageByClientDto(
    val from: String,
    val to: String,
    val packetId: String,
    val type: String,
    val body: String,
    val id: Int?,
)

data class Message(
    val from: String,
    val to: String,
    val packetId: String,
    val type: String,
    val body: String,
    val id: Int,
    val time: Int,
    val isSeen: Boolean
)

data class Seen(
    val messageId: Int,
    val chatId: String,
    val packetId: String,
)

data class CommentDto(
    val postId: String,
    val from: String,
    val time: Int,
    val commentId: String,
    val body: String,

    )

data class ClientPacket(
    val message: MessageByClientDto? = null,
    val seen: Seen? = null,
    val comment: CommentDto? = null,

    )

data class Ack(
    val packetId: String,
    val chatId: String,
    val id: Int
)

data class Packet(
    val message: Message? = null,
    val seen: Seen? = null,
    val ack: Ack? = null,
)


data class NewPostDao(
    val fileUuid: String,
    val ownerId: String,
    val description: String,
    val type: String,
    val title: String,
    val location: Location = Location()

)

