package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Doctor(
    @Id
    var ownerId: String = "",
    var rate: Int = 0,
    var info: String = "",
    var description: String = ""

)