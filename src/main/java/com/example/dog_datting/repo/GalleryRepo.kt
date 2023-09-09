package com.example.dog_datting.repo

import com.example.dog_datting.db.Gallery
import com.example.dog_datting.db.User
import org.springframework.data.jpa.repository.JpaRepository

interface GalleryRepo : JpaRepository<Gallery, Long> {
    fun getByUser(user: User): List<Gallery>?
}