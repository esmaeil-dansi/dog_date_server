package com.example.dog_datting.db

import com.example.dog_datting.models.PlaceType
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

    var submitted: Boolean = false,

    var type: PlaceType = PlaceType.ALL,
    var palaceType: String = "",

    var phone: String = ""
)
