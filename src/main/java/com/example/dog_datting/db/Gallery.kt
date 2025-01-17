package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class Gallery(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var user: String = "",

    var time: Long = 0,

    var fileInfo: String = "",

    var comment: String = "",

    )
