package com.example.dog_datting.services

import com.example.dog_datting.DogDatting
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service


@Service
class FirebaseMessagingService(private val firebaseMessaging: FirebaseMessaging) {
    val logger: Logger = LogManager.getLogger(DogDatting::class.java)
    fun sendNotification(title: String, body: String, token: String) {
        try {
            val notification: Notification = Notification
                .builder()
                .setTitle(title)
                .setBody(body)
                .build()
            val message: Message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putData(title, body)
                .build()
            firebaseMessaging.send(message)
            logger.info("Send successful firebase message")

        } catch (e: Exception) {
            logger.error(e.message)
        }

    }
}