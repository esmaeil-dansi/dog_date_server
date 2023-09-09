package com.example.dog_datting.repo

import com.example.dog_datting.db.Notifications
import com.example.dog_datting.db.User
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepo : JpaRepository<Notifications, Long> {

    fun getBySenderOrReceiver(sender: User, receiver: User): List<Notifications>?
}