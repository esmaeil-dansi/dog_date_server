package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class Doctor(
    @Id
    var ownerId: String = "",
    var rate: Int = 0,
    var description: String = "",
    var name: String = "",
    var avatarInfo: String = "",
    @ManyToOne
    var location: Location = Location()

)