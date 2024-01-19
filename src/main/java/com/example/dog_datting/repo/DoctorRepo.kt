package com.example.dog_datting.repo

import com.example.dog_datting.db.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepo : JpaRepository<Doctor, Long> {
    fun findBySubmittedTrue(): List<Doctor>?
}