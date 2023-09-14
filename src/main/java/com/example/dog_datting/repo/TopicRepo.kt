package com.example.dog_datting.repo


import com.example.dog_datting.db.Topics
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepo : JpaRepository<Topics, Long> {
    fun getByName(name: String): Topics?

}