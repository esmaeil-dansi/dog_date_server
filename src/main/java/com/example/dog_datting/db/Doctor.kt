package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Doctor(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var ownerId: String = "",
    var rate: Int = 0,
    var description: String = "",
    var name: String = "",
    var avatarInfo: String = "",
    @ManyToOne
    var location: Location = Location(),

    @ManyToOne
    var locationInfo: Location? = null,
    var locationDetails: String = "",
    var submitted: Boolean = false,
    var phone: String = ""

)