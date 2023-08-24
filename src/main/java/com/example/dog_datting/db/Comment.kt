package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Comment(
    @GeneratedValue
    @Id
    private val id: Long = 0,
    var postId: String = "",
    var body: String = "",
    var from: String = "",
    var time: Long = 0,

    )