package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Shop(
    @Id
    private val id: Long = 0,
    var shopId: String = "",
    var ownerId: String = "",
    var description: String = ""
)