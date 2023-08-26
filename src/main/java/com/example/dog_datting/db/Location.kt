package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Location(
    @Id
    @GeneratedValue
    val id: Long = 0,
    var lat: Double = 0.0,
    var lon: Double = 0.0
)