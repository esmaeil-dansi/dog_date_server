package com.example.dog_datting.repo

import com.example.dog_datting.db.Notifications
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepo : JpaRepository<Notifications, Long> {
    fun getByReceiver(receiver: String): List<Notifications>?
}