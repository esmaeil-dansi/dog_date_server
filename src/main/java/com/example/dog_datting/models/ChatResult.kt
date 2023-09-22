package com.example.dog_datting.models

data class ChatResult(
    var userId: String,
    var message: String,
    var name: String,
    var lastTime: Long,
    var lastMessageId: Int
)
