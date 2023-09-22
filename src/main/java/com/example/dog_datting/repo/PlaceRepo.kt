package com.example.dog_datting.repo

import com.example.dog_datting.db.Place
import com.example.dog_datting.db.Post
import org.springframework.data.jpa.repository.JpaRepository

sealed interface PlaceRepo : JpaRepository<Place, Long> {
    fun findByIdGreaterThanOrderByIdDesc(id: Long): List<Place>?
}