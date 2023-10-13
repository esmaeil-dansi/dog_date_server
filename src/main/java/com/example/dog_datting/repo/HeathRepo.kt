package com.example.dog_datting.repo

import com.example.dog_datting.db.Heath
import org.springframework.data.jpa.repository.JpaRepository

interface HeathRepo : JpaRepository<Heath, Long> {
    fun getByAnimalUid(animalUid: String): List<Heath>?
}