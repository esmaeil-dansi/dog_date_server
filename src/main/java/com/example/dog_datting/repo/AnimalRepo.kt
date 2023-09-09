package com.example.dog_datting.repo

import com.example.dog_datting.db.Animal
import org.springframework.data.jpa.repository.JpaRepository

interface AnimalRepo : JpaRepository<Animal, Long> {
}