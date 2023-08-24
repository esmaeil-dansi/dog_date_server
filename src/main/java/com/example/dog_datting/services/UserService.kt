package com.example.dog_datting.services

import org.springframework.stereotype.Service

@Service
class UserService {
    fun sendVerificationCodeToEmail(email: String, code: String) {}

    fun sendVerificationCodeToPhone(phone: String, code: String) {}
}