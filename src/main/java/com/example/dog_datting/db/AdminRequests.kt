package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
data class AdminRequests(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var time: Long = 0,

    var requester: String = "",

    @ManyToOne
    var place: Place = Place(),

    var type: AdminRequestType = AdminRequestType.PLACE

)