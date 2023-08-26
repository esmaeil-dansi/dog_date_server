package com.example.dog_datting.repo


import com.example.dog_datting.db.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepo : JpaRepository<Comment, Long> {
    fun getCommentsByPostIdByOrderByTimeDesc(postId: String): List<Comment>?
}