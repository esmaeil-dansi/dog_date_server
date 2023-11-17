package com.example.dog_datting.models

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Place
import javax.persistence.ManyToOne

data class AdminRequestsRes (
    var id: Long = 0,

    var time: Long = 0,

    var requester: String = "",

    var place: PlaceRes = PlaceRes(),

    var type: AdminRequestType = AdminRequestType.PLACE
)