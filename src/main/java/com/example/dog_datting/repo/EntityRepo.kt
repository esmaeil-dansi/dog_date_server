package com.example.dog_datting.repo

import com.example.dog_datting.db.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface EntityRepo : JpaRepository<Chat, String> {

}