package com.example.dog_datting.controller

import com.example.dog_datting.dto.ClientPacket
import com.example.dog_datting.services.MessageService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.RestController


@RestController
class MessageController(
    private val messageService: MessageService,
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
}