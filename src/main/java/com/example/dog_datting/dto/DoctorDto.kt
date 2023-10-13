package com.example.dog_datting.dto

import com.example.dog_datting.models.Location
import javax.swing.ViewportLayout

class DoctorDto(
    val name: String,
    val avatarInfo: String,
    val description: String,
    val ownerId: String,
    val location: Location = Location(),
    val locationInfo: Location? = null,
    val locationDetails: String = ""
)

data class RateDoctorDto(val ownerId: String, val rate: Int, val requester: String)

