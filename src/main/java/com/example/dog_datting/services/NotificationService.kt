package com.example.dog_datting.services

import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Notifications
import com.example.dog_datting.db.User
import com.example.dog_datting.dto.NotificationDto
import com.example.dog_datting.models.NotificationType
import com.example.dog_datting.repo.LocationRepo
import com.example.dog_datting.repo.NotificationRepo
import com.example.dog_datting.repo.UserRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class NotificationService(
    private val notificationRepo: NotificationRepo,
    private val userRepo: UserRepo,
    private val locationRepo: LocationRepo,
    private val messageService: MessageService
) {
    val logger: Logger = LogManager.getLogger(NotificationService::class.java)

    fun distance(
        lat1: Double, lat2: Double, lon1: Double,
        lon2: Double,
    ): Double {
        val r = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = r * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        return sqrt(distance)
    }

    fun processNotification(notificationDto: NotificationDto, sender: String) {
        try {
            var users: List<User>? = userRepo.findByLocationIsNotNull()
            if (users != null) {
                logger.info("all usr size" + users.size)
                if (notificationDto.location != null) {
                    logger.info("location not nulllllllllllll")
                    users.forEach { u ->
                        if (distance(
                                notificationDto.location.lat,
                                u.location!!.lat,
                                notificationDto.location.lon,
                                u.location!!.lon
                            ) > 0
                        ) {
                            val location = locationRepo.save(
                                Location(
                                    lon = notificationDto.location.lon,
                                    lat = notificationDto.location.lat
                                )
                            )
                            val notifications = Notifications(
                                body = notificationDto.body,
                                location = location,
                                packetId = notificationDto.packetId,
                                sender = sender,
                                receiver = u.uuid,
                                type = NotificationType.valueOf(notificationDto.type),
                                time = System.currentTimeMillis(),
                                fileInfo = notificationDto.fileInfo
                            )
                            logger.info("send notification\n");
                            val res = notificationRepo.save(notifications);
                            messageService.sendNotification(notifications = res, to = u.uuid)
                        }


                    }
                }

            }
        } catch (e: Exception) {
            logger.error(e.message)
        }


    }
}