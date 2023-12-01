package com.example.dog_datting.dto

import com.example.dog_datting.db.Notifications
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
    val time: Long,
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
    val time: Long,
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
    val notification: Notifications? = null,
)


data class NewPostDao(
    val fileUuid: String,
    val ownerId: String,
    val description: String,
    val type: String,
    val title: String,
    val location: Location = Location(),
    val locationInfo: Location? = null,
    val topics: List<String> = ArrayList()

)


data class NewPlaceDto(
    val fileUuid: String,
    val owner: String,
    val description: String,
    val name: String,
    val location: Location = Location(),
    val locationInfo: Location? = null,
    val type: String = "ALL"
)

data class NewShopDto(
    val ownerId: String,
    val title: String,
    val shopId: String,
    val description: String,
    val avatar: String,
)

data class NewShopItemDto(
    val shopId: String,
    val details: String,
    val price: Double,
    val name: String,
    val fileUuid: String,
)

