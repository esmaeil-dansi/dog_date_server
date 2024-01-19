package com.example.dog_datting.db

import org.checkerframework.checker.units.qual.N
import javax.annotation.Nullable
import javax.persistence.*


@Entity
data class AdminRequests(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var time: Long = 0,

    var requester: String = "",

    @OneToOne
    @Nullable
    var place: Place? = null,

    @OneToOne
    @Nullable
    var shop: Shop? = null,


    @OneToOne
    @Nullable
    var doctor: Doctor? = null,

    var type: AdminRequestType = AdminRequestType.PLACE

)