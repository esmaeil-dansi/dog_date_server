package com.example.dog_datting.controller

import com.example.dog_datting.db.Chat
import com.example.dog_datting.db.Notifications
import com.example.dog_datting.dto.ClientPacket
import com.example.dog_datting.dto.Message
import com.example.dog_datting.models.ChatResult
import com.example.dog_datting.repo.ChatRepo
import com.example.dog_datting.repo.MessageRepo
import com.example.dog_datting.repo.NotificationRepo
import com.example.dog_datting.services.MessageService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@RestController
class MessageController(
    private val messageService: MessageService,
    private val messageRepo: MessageRepo,
    private val chatRepo: ChatRepo,
    private val notificationRepo: NotificationRepo,
) {
    val logger: Logger = LogManager.getLogger(MessageController::class.java)
    val executorService: ExecutorService = Executors.newFixedThreadPool(10)

    @MessageMapping("/private-message")
    fun receivePrivateMessage(@Payload clientPacket: ClientPacket) {
        executorService.execute {
            if (clientPacket.message != null) {
                messageService.proxyMessage(message = clientPacket.message)
            } else if (clientPacket.seen != null) {
                messageService.processSeen(seen = clientPacket.seen)
            } else if (clientPacket.comment != null) {
                messageService.processComment(comment = clientPacket.comment)
            }
        }

    }

    @GetMapping("fetchChats/{uuid}")
    @ResponseBody
    fun fetchChat(@PathVariable(value = "uuid") uuid: String): List<ChatResult>? {
        try {
            val res: MutableList<ChatResult> = mutableListOf()
            chatRepo.getByOwnerIdOrUserId(uuid, uuid)?.forEach { chat ->
                run {
                    var userId: String = chat.userId
                    if (userId == uuid) {
                        userId = chat.ownerId
                    }
                    res.add(
                        ChatResult(
                            message = chat.lastMessage,
                            userId = userId,
                            name = "",
                            lastTime = chat.lastTime,
                            lastMessageId = chat.lastMessageId
                        )
                    )
                }
            }
            return res
        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }


    @GetMapping(path = ["/fetchMessages/{ownerId}/{userId}/{lastMessageId}"])
    @ResponseBody
    fun fetchStory(
        @PathVariable(value = "ownerId") ownerId: String,
        @PathVariable(value = "userId") userId: String,
        @PathVariable(value = "lastMessageId") lastMessageId: Int,
    ): List<Message>? {
        try {
            var chat: Chat? = chatRepo.getByOwnerIdAndUserId(ownerId = ownerId, userId = userId)
            if (chat == null) {
                chat = chatRepo.getByOwnerIdAndUserId(ownerId = userId, userId = ownerId)
            }
            if (chat != null) {
                if (chat.lastMessageId != lastMessageId) {
                    val messages: List<com.example.dog_datting.db.Message>? =
                        messageRepo.getMessageByChatAndMessageIdGreaterThanOrderByMessageIdDesc(chat, lastMessageId)
                    if (!messages.isNullOrEmpty()) {
                        return messages.map { s -> messageService.convertMessage(s) }
                    }
                }

            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }


    @GetMapping("/getAllNotifications/{requester}")
    @ResponseBody
    fun getAllNotifications(@PathVariable(value = "requester") requester: String): List<Notifications>? {
        try {
            return notificationRepo.getByReceiver(requester)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }


    @PostMapping("/seenNotification/{id}")
    @ResponseBody
    fun seenNotification(@PathVariable(value = "id") id: Long) {
        var notification = notificationRepo.findById(id)
        if (notification.isPresent) {
            notificationRepo.save(notification.get().copy(isSeen = true))
        }

    }


}