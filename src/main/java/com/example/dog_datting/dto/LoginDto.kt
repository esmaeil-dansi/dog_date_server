package com.example.dog_datting.dto

import com.example.dog_datting.models.Location

data class EmailDto(val email: String, val password: String, val username: String = "", val name: String = "")

data class VerificationDto(val user: String, val code: Int)

data class RecoveryDto(val email: String, val password: String, val code: Int)

data class PhoneNumberDto(val phoneNumber: String, val password: String)

data class LoginDto(val email: String = "", val password: String)

data class GalleryDto(val fileInfo: String, val comment: String)

data class NewAnimalDto(
    val id: Long = 0,
    val name: String = "",
    val owner: String = "",
    val description: String = "",
    val type: String = "",
    val avatarId: String = "",
    val breed: String = "",
    val sex: String = "",
    val passed: Boolean = false,
    val death: Long = 0,
    val birthDay: Long = 0,
    val lose: Boolean = false,
    val neutered: Boolean = false,
)

data class NotificationDto(
    val location: Location? = null,
    val body: String = "",
    val fileInfo: String = "",
    val type: String,
    val packetId: String
)
