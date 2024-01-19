package com.example.dog_datting.db

import com.example.dog_datting.models.NotificationType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Notifications(
    @GeneratedValue
    @Id
    var id: Long = 0,
    var receiver: String = "",
    var postId: Long = 0,
    var placeId: Long = 0,
    var type: NotificationType = NotificationType.NEWS,
    var isSeen: Boolean = false,
    var time: Long = 0
)