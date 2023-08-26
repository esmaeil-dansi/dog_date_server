package com.example.dog_datting.controller

import com.example.dog_datting.db.Chat
import com.example.dog_datting.dto.ClientPacket
import com.example.dog_datting.dto.Message
import com.example.dog_datting.repo.ChatRepo
import com.example.dog_datting.repo.MessageRepo
import com.example.dog_datting.services.MessageService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class MessageController(
    private val messageService: MessageService,
    private val messageRepo: MessageRepo,
    private val chatRepo: ChatRepo
) {
    val logger: Logger = LogManager.getLogger(MessageController::class.java)

    @MessageMapping("/private-message")
    fun receivePrivateMessage(@Payload clientPacket: ClientPacket) {
        logger.info("new private message........")
        if (clientPacket.message != null) {
            messageService.proxyMessage(message = clientPacket.message)
        } else if (clientPacket.seen != null) {
            messageService.processSeen(seen = clientPacket.seen)
        } else if (clientPacket.comment != null) {
            messageService.processComment(comment = clientPacket.comment)
        }

    }


    @GetMapping(path = ["/fetchMessages/{ownerId}/{userId}"])
    @ResponseBody
    fun fetchStory(
        @PathVariable(value = "ownerId") ownerId: String,
        @PathVariable(value = "userId") userId: String
    ): List<Message>? {
        try {
            val chat: Chat? = chatRepo.getByOwnerIdAndUserId(ownerId = ownerId, userId = userId)
            if (chat != null) {
                var messages: List<com.example.dog_datting.db.Message>? = messageRepo.getMessageByChat(chat)
                if (!messages.isNullOrEmpty()) {
                    return messages.map { s -> messageService.convertMessage(s) }
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }


}