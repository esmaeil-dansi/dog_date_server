package com.example.dog_datting.dto

import com.example.dog_datting.models.AnimalType
import com.example.dog_datting.models.Location

data class EmailDto(val email: String, val password: String, val username: String = "", val name: String = "")

data class VerificationDto(val user: String, val code: Int)
data class VerificationRes(val uid: String, val accessToken: String)

data class RecoveryDto(val email: String, val password: String, val code: Int)

data class PhoneNumberDto(val phoneNumber: String, val password: String)

data class LoginDto(val email: String = "", val password: String)


data class GalleryDto(val fileInfo: String, val comment: String)

data class UpdateLookReq(val mate: Boolean, val walk: Boolean, val playingPartner: Boolean)
data class HeathDto(val fileUuid: String, val body: String)
class DoctorDto(
    val name: String,
    val avatarInfo: String,
    val description: String,
    val phone: String,
    val email: String,
    val location: Location = Location(),
    val locationInfo: Location? = null,
    val locationDetails: String = "",
    val requester: String = ""
)

data class RateDoctorDto(val id: Long, val rate: Int, val requester: String)
data class SearchUserDto(
    val location: Location,
    val have: AnimalType,
    val looking: String = "",
    val gender: String = ""
)

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
    val uid: String = ""
)


data class SettingsDto(val showAd: Boolean, val adLoadingTimer: Int, val openAppId: String, val bannerId: String)

class StoryDto(
    val userId: String,
    val fileInfo: String,
    val description: String,
    val time: Long
)
