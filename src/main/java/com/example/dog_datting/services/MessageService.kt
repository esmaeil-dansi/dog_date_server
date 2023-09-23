package com.example.dog_datting.services


import com.example.dog_datting.db.Chat
import com.example.dog_datting.db.Comment
import com.example.dog_datting.db.Message
import com.example.dog_datting.db.Notifications
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.MessageType
import com.example.dog_datting.repo.ChatRepo
import com.example.dog_datting.repo.CommentRepo
import com.example.dog_datting.repo.MessageRepo
import com.example.dog_datting.repo.UserRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepo: MessageRepo,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private var chatRepository: ChatRepo,
    private var commentRepo: CommentRepo,
    private var userRepo: UserRepo,
    private var firebaseMessagingService: FirebaseMessagingService
) {
    val logger: Logger = LogManager.getLogger(MessageService::class.java)
    fun proxyMessage(message: MessageByClientDto) {
        val time: Long = System.currentTimeMillis()
        var lastMessageId = 1
        var chat = chatRepository.getByOwnerIdAndUserId(message.from, message.to)
        if (chat == null) {
            chat = chatRepository.getByOwnerIdAndUserId(message.to, message.from)
        }
        if (chat != null) {
            lastMessageId = chat.lastMessageId + 1
        }
        var last = message.body;
        if (message.type != "TEXT") {
            last = "file";
        }

        chat = if (chat != null) {
            chatRepository.save(
                Chat(
                    id = chat.id,
                    lastMessageId = lastMessageId,
                    ownerId = message.from,
                    userId = message.to,
                    lastTime = time,
                    lastMessage = last
                )
            )
        } else {
            chatRepository.save(
                Chat(
                    lastMessageId = lastMessageId, ownerId = message.from, userId = message.to, lastTime = time,
                    lastMessage = last
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

        sendFirebase(message.from, message.to, message.body)

    }

    fun sendFirebase(sender: String, receiver: String, body: String) {
        var user1 = userRepo.getUserByUuid(sender)
        var user2 = userRepo.getUserByUuid(receiver)
        if (user1 != null && user2 != null && user2.firebaseToken.isNotEmpty()) {
            firebaseMessagingService.sendNotification(user1.firstname, body, user2.firebaseToken);
        }

    }

    fun sendNotification(notifications: Notifications, to: String) {
        simpMessagingTemplate.convertAndSendToUser(
            to, "/private", Packet(notification = notifications)
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
                time = comment.time
            )
        )
        simpMessagingTemplate.convertAndSendToUser(
            comment.postId, "/comment", comment
        )

    }


    fun convertMessage(message: Message): com.example.dog_datting.dto.Message =
        Message(
            from = message.from,
            to = message.to,
            body = message.body,
            packetId = message.packetId,
            type = message.type.name,
            id = message.messageId,
            time = message.time,
            isSeen = message.isSeen
        )


}