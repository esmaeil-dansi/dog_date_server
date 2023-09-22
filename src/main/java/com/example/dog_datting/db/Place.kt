package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
data class Place(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var name: String = "",

    var fileUuid: String = "",

    var description: String = "",

    @ManyToOne
    var location: Location = Location(),

    @ManyToOne
    var locationInfo: Location? = null,

    var owner: String = "",

    var submitted: Boolean = false
)
