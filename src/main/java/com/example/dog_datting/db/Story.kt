package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class Story(
    @Id var userId: String = "",
    var fileInfo: String = "",
    var time: Long = 0,
    var description: String = ""
)