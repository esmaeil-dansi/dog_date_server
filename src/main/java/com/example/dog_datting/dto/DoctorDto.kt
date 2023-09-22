package com.example.dog_datting.dto

import com.example.dog_datting.models.Location

class DoctorDto(
    val name: String,
    val avatarInfo: String,
    val description: String,
    val ownerId: String,
    val location: Location = Location(),
    val locationInfo: Location? = null,
)

data class RateDoctorDto(val ownerId: String, val rate: Int)

