package com.example.dog_datting.services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class EmailService {
    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    @Value("\${spring.mail.username}")
    private lateinit var sender: String

    fun sendLoginCodeEmail(email: String, code: String) {
        try {
            val mailMessage = SimpleMailMessage()
            mailMessage.from = sender
            mailMessage.setTo(email)
            mailMessage.text = code
            mailMessage.subject = "dog dating"

            // Sending the mail
            javaMailSender.send(mailMessage)

        } // Catch block to handle the exceptions
        catch (e: Exception) {
            print(e.message)
        }
    }


    fun sendVerifyCodeEmail(email: String, code: String) {
        try {
            val mailMessage = SimpleMailMessage()
            mailMessage.from = sender
            mailMessage.setTo(email)
            mailMessage.text = code
            mailMessage.subject = "dog dating recovery code"

            // Sending the mail
            javaMailSender.send(mailMessage)

        } // Catch block to handle the exceptions
        catch (e: Exception) {
            print(e.message)
        }
    }


}