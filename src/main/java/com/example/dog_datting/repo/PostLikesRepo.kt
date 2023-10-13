package com.example.dog_datting.repo

import com.example.dog_datting.db.Post
import com.example.dog_datting.db.PostLikes
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikesRepo : JpaRepository<PostLikes, Long> {
    fun countGetByPost(post: Post): Int

    fun getByUserIdAndPost(userId: String, post: Post): PostLikes?
}