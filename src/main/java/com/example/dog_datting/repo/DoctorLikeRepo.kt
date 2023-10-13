package com.example.dog_datting.repo

import com.example.dog_datting.db.DoctorLikes
import com.example.dog_datting.db.Post
import com.example.dog_datting.db.PostLikes
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorLikeRepo : JpaRepository<DoctorLikes, Long> {
    fun countGetByDoctorId(doctorId: String): Int

    fun getByUserIdAndDoctorId(userId: String, doctorId: String): DoctorLikes?

}