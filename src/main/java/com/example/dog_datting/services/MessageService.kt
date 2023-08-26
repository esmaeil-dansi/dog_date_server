package com.example.dog_datting.services

import com.example.dog_datting.controller.MainController
import com.example.dog_datting.db.Chat
import com.example.dog_datting.db.Comment
import com.example.dog_datting.db.Message
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.MessageType
import com.example.dog_datting.repo.ChatRepo
import com.example.dog_datting.repo.CommentRepo
import com.example.dog_datting.repo.MessageRepo
import com.example.dog_datting.repo.PostRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepo: MessageRepo,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private var chatRepository: ChatRepo,
    private var commentRepo: CommentRepo
) {
    val logger: Logger = LogManager.getLogger(MessageService::class.java)
    fun proxyMessage(message: MessageByClientDto) {
        val time: Long = System.currentTimeMillis()
        var lastMessageId = 1
        var chat = chatRepository.getByOwnerIdAndUserId(message.from, message.to)
        if (chat != null) {
            lastMessageId = chat.lastMessageId + 1
        }
        chat = if (chat != null) {
            chatRepository.save(
                Chat(
                    id = chat.id,
                    lastMessageId = lastMessageId,
                    ownerId = message.from,
                    userId = message.to,
                    lastTime = time,
                    lastMessage = JSONObject(message).toString()
                )
            )
        } else {
            chatRepository.save(
                Chat(
                    lastMessageId = lastMessageId, ownerId = message.from, userId = message.to, lastTime = time,
                    lastMessage = JSONObject(message).toString()
                )
            )
        }


        val msg = Message(
            messageId = lastMessageId,
            from = message.from,
            to = message.to,
            type = MessageType.valueOf(message.type),
            body = message.body,
            packetId = message.packetId,
            chat = chat,
            isSeen = false,
            time = System.currentTimeMillis()
        )
        messageRepo.save(msg)
        logger.info("proxy Message" + message.body)
        simpMessagingTemplate.convertAndSendToUser(
            message.to, "/private", Packet(message = convertMessage(msg))
        )

        simpMessagingTemplate.convertAndSendToUser(
            message.from,
            "/private",
            Packet(ack = Ack(packetId = message.packetId, id = msg.messageId, chatId = chat.userId))
        )
    }

    fun processSeen(seen: Seen) {
        val msg: Message? = messageRepo.getMessageByMessageIdAndFrom(seen.messageId, seen.chatId)
        if (msg != null) {
            messageRepo.save(msg.copy(isSeen = true))
            simpMessagingTemplate.convertAndSendToUser(
                seen.chatId, "/private", Packet(seen = seen)
            )
        }

    }

    fun processComment(comment: CommentDto) {
        logger.info("process comment")
        commentRepo.save(
            Comment(
                from = comment.from,
                body = comment.body,
                postId = comment.postId,
                time = comment.time.toLong()
            )
        )
        simpMessagingTemplate.convertAndSendToUser(
            comment.postId, "/comment", comment
        )

    }


    private fun convertMessage(message: Message): com.example.dog_datting.dto.Message =
        Message(
            from = message.from,
            to = message.to,
            body = message.body,
            packetId = message.packetId,
            type = message.type.name,
            id = message.id,
            time = message.time.toInt(),
            isSeen = message.isSeen
        )


}