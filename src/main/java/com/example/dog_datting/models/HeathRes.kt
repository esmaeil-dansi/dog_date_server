package com.example.dog_datting.models

data class HeathRes(
    val id: Long = 0,
    var animalUid: String = "",
    var body: String = "",
    var fileUuids: List<String> = ArrayList()
)