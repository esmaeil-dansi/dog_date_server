package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Location {
    @Id
    @GeneratedValue
    val id: Long = 0
    var lat: Long = 0
    var lag: Long = 0
}