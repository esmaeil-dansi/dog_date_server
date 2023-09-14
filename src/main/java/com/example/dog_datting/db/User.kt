package com.example.dog_datting.db

import javax.persistence.*

@Entity(name = "user")
data class User(
    @Id
    @GeneratedValue
    var id: Long = 0,
    var uuid: String = "",
    var verificationCode: Int = 0,
    var firstname: String = "",
    var lastname: String = "",
    var password: String = "",
    var username: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var recoveryCode: Int = 0,
    var info: String = "",
    var interests: String = "",
    @ManyToOne
    var location: Location? = null
)