package com.example.dog_datting.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig  {


    @Bean
    @Throws(java.lang.Exception::class)
     fun configure(http: HttpSecurity):SecurityFilterChain {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/login", "/sendVerificationCode", "/recoveryEmail/", "/recovery","/singingByEmail")
            .permitAll() // Exclude login and token endpoints from authentication
            .anyRequest().authenticated()
        http.addFilterBefore(JwtRequestFilter(), AnonymousAuthenticationFilter::class.java)
        return http.build()
    }
}
