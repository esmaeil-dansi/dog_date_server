package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
data class PostLikes(
    @GeneratedValue
    @Id var id: Long = 0,

    @ManyToOne
    val post: Post = Post(),

    val userId: String = ""

)
