package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Topics(
    @GeneratedValue
    @Id
    var id: Long = 0,
    var name: String = "",
)