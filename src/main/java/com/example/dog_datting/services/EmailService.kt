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
            mailMessage.subject = "login code"
            javaMailSender.send(mailMessage)
        } catch (e: Exception) {
            print(e.message)
        }
    }


    fun sendVerifyCodeEmail(email: String, code: String) {
        try {
            val mailMessage = SimpleMailMessage()
            mailMessage.from = sender
            mailMessage.setTo(email)
            mailMessage.text = code
            mailMessage.subject = "recovery code"
            javaMailSender.send(mailMessage)
        } catch (e: Exception) {
            print(e.message)
        }
    }


}
