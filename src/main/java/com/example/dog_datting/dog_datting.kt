package com.example.dog_datting



import com.example.dog_datting.utils.JwtUtil
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.SpringApplication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource

@SpringBootApplication
class DogDatting {
    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val googleCredentials = GoogleCredentials
            .fromStream(ClassPathResource("firebase-service-account.json").inputStream)
        val firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build()
        val app = FirebaseApp.initializeApp(firebaseOptions, "my-app")
        return FirebaseMessaging.getInstance(app)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(DogDatting::class.java, *args)
}