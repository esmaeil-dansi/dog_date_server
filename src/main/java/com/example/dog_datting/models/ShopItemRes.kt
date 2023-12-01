package com.example.dog_datting.models

data class ShopItemRes(
    val id: Long,
    val price: Double,
    val name: String,
    val details: String,
    var fileUuids: List<String> = ArrayList()
)
