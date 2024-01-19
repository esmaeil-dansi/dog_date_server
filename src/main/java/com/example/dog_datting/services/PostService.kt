package com.example.dog_datting.services

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
import kotlin.math.*

@Service
class PostService(
    private val notificationRepo: NotificationRepo,
    private val userRepo: UserRepo,
    private val messageService: MessageService
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
                userRepo.findByLocationIsNotNull()?.forEach { u ->
                    if (checkLocation(u.location!!, post)) {
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
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    fun checkLocation(userLocation: Location, post: Post): Boolean {
        return inNearRadius(
            post.location.lat,
            userLocation.lat,
            post.location.lon,
            userLocation.lon
        ) || (post.locationInfo != null && inNearRadius(
            post.locationInfo!!.lat,
            userLocation.lat,
            post.locationInfo!!.lon,
            userLocation.lon
        ))


    }

    fun inNearRadius(
        lat1: Double, lat2: Double, lon1: Double,
        lon2: Double,
    ): Boolean {
        val r = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = r * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        return sqrt(distance) < 50000
    }
}