package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Shop(
    @Id
    @GeneratedValue
    val id: Long = 0,
    var shopId: String = "",
    var ownerId: String = "",
    var description: String = "",
    var submitted: Boolean = false,
    var name: String = "",
    var avatar: String = ""
)