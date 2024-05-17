package com.example.dog_datting.security

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtRequestFilter : OncePerRequestFilter() {
    val jwtUtils = com.example.dog_datting.utils.JwtUtil()
    val logger: Logger = LogManager.getLogger(JwtRequestFilter::class.java)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken: String? = jwtUtils.resolveToken(request)
            if (accessToken == null || accessToken.isEmpty() || jwtUtils.getUid(accessToken) == null) {
                filterChain.doFilter(request, response)
                return
            } else {
                val authentication = AppAuthenticationToken(accessToken, null, ArrayList())
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error(e.message)


        }
        filterChain.doFilter(request, response)
    }
}