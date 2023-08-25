package com.example.dog_datting.repo

import com.example.dog_datting.db.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepo : JpaRepository<Post, Int> {
    fun findByIdGreaterThanOrderByIdDesc(id: Long): List<Post>?
}