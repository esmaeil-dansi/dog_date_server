package com.example.dog_datting.models

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Doctor
import com.example.dog_datting.db.Shop

data class AdminRequestsRes(
    var id: Long = 0,
    var time: Long = 0,
    var requester: String = "",
    var place: PlaceRes? = null,
    var shop: ShopRes? = null,
    var doctor: Doctor? = null,
    var type: AdminRequestType = AdminRequestType.PLACE
)