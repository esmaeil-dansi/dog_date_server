package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ShopItems(
    @Id
    var shopItemId: String = "",
    var shopId: String = "",
    var price: Int = 0,
    var info: String = "",
    var description: String = "",


    )
