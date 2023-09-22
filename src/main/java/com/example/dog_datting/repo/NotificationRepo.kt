package com.example.dog_datting.repo

import com.example.dog_datting.db.Notifications
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepo : JpaRepository<Notifications, Long> {

    fun getBySenderOrReceiver(sender: String, receiver: String): List<Notifications>?
}