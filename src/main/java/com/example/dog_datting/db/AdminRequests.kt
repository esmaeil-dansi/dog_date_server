package com.example.dog_datting.db

import org.checkerframework.checker.units.qual.N
import javax.annotation.Nullable
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
    @Nullable
    var place: Place? = null,

    @ManyToOne
    @Nullable
    var shop: Shop? = null,

    var type: AdminRequestType = AdminRequestType.PLACE

)