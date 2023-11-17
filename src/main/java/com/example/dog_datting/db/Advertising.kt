package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Advertising(
    @GeneratedValue
    @Id
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var fileUuid: String = ""
)
