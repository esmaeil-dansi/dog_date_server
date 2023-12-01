package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ShopItems(
    @Id
    @GeneratedValue
    var id: Long = 0,
    var shopId: String = "",
    var price: Double = 0.0,
    var details: String = "",
    var name: String = "",
    var fileUuid: String = "",
)

