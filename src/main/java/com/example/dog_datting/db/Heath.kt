package com.example.dog_datting.db

import javax.persistence.*

@Entity
data class Heath(
    @GeneratedValue
    @Id
    var id: Long = 0,

    var animalUid: String = "",
    @Lob
    var body: String = "",
    var fileUuid: String = "",
)
