package com.example.dog_datting.db

import com.example.dog_datting.models.NotificationType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Notifications(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var sender: String = "",

    var packetId: String = "",
    var receiver: String = "",

    var type: NotificationType = NotificationType.NEWS,
    var time: Long = 0,

    var body: String = "",

    @ManyToOne
    var location: Location? = Location(),

    var fileInfo: String = "",

    )