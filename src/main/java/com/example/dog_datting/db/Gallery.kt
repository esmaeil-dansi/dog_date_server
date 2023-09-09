package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
data class Gallery(
    @GeneratedValue
    @Id
    var id: Long = 0,
    @ManyToOne
    var user: User = User(),

    var time: Long = 0,

    var fileinfo: String = "",

    var comment: String = "",

    )
