package com.example.dog_datting.db

import com.example.dog_datting.models.Animal
import com.example.dog_datting.models.Gender
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "animal")
data class Animals(
    @Id
    var animalID: Long = 0,
    var name: String = "",
    var animal: Animal = Animal.CAT,
    @ManyToOne
    var user: User = User(),
    var ege: Int = 0,
    var gender: Gender = Gender.FEMALE,
)