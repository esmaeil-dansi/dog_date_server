package com.example.dog_datting.services

import com.example.dog_datting.db.Animal
import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Notifications
import com.example.dog_datting.db.Post
import com.example.dog_datting.models.NotificationType
import com.example.dog_datting.models.PostType
import com.example.dog_datting.repo.NotificationRepo
import com.example.dog_datting.repo.UserRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class PostService(
    private val notificationRepo: NotificationRepo,
    private val userRepo: UserRepo,
    private val messageService: MessageService,
    private val locationService: LocationService,
    private val firebaseMessagingService: FirebaseMessagingService
) {
    val logger: Logger = LogManager.getLogger(PostService::class.java)

    @Async
    fun processPost(post: Post) {
        try {
            logger.info("process new Post.......................\n")
            if (post.type == PostType.DANGER || post.type == PostType.LOST) {
                var type: NotificationType = NotificationType.WARNING
                if (post.type == PostType.LOST) {
                    type = NotificationType.LOSE
                }
                logger.info("crate new notification.......")
                creteNotification(post, type)
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    private fun creteNotification(post: Post, type: NotificationType) {
        userRepo.findByLocationIsNotNull()?.forEach { u ->
            if (locationService.checkLocation(u.location!!, post.location)) {
                val notifications = Notifications(
                    postId = post.id,
                    receiver = u.uuid,
                    type = type,
                    time = System.currentTimeMillis()
                )
                logger.info("send & save  notification\n")
                messageService.sendNotification(
                    notifications = notificationRepo.save(notifications),
                    to = u.uuid
                )
                if (u.firebaseToken.isNotEmpty()) {
                    if (type == NotificationType.LOSE) {
                        firebaseMessagingService.sendNotification(
                            "Lost",
                            "A animal lost !",
                            u.firebaseToken
                        )

                    } else {
                        firebaseMessagingService.sendNotification(
                            type.name.toLowerCase(),
                            "Warning for animals !",
                            u.firebaseToken
                        )

                    }

                }
            }
        }
    }




}