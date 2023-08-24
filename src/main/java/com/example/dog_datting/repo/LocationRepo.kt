package com.example.dog_datting.repo

import com.example.dog_datting.db.Location
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepo : JpaRepository<Location, Long> {
}