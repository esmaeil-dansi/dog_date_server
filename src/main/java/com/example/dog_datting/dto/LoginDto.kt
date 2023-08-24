package com.example.dog_datting.dto

data class EmailDto(val email: String, val password: String)

data class VerificationDto(val user: String, val code: Int)

data class RecoveryDto(val email: String, val password: String, val code: Int)

data class PhoneNumberDto(val phoneNumber: String, val password: String)

data class LoginDto(val email: String = "", val password: String)