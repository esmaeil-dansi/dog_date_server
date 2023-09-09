package com.example.dog_datting.db

import com.example.dog_datting.models.AnimalType
import com.example.dog_datting.models.Gender
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Animal(
    @GeneratedValue
    @Id
    var id: Long = 0,
    var name: String = "",
    var breed: String,
    var type: AnimalType = AnimalType.CAT,
    @ManyToOne
    var owner: User = User(),
    var birthDay: String = "",
    var passes: Boolean = false,
    var description: String = "",
    var sex: Gender = Gender.FEMALE,
    var death: String = "",
    var neutered: Boolean = false,
    var avatarId: String = "",
    var uid: String = ""
)