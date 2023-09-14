package com.example.dog_datting.db

import com.example.dog_datting.models.AnimalType
import com.example.dog_datting.models.Gender
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Animal(
    @GeneratedValue
    @Id
    var id: Long = 0,
    var name: String = "",
    var breed: String,
    var type: AnimalType = AnimalType.CAT,
    var owner: String = "",
    var birthDay: String = "",
    var passed: Boolean = false,
    var description: String = "",
    var sex: Gender = Gender.FEMALE,
    var death: String = "",
    var neutered: Boolean = false,
    var avatarId: String = "",
    var uid: String = "",
    var lose :Boolean = false
)