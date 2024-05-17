package com.example.dog_datting.utils


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import javax.servlet.http.HttpServletRequest


class JwtUtil {
    private val TOKEN_HEADER = "Authorization"
    private val SECRET = "oijweiorjfmerhnweinfewufnweofnweujfewfewiofjewffjejn"

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .signWith(SignatureAlgorithm.HS512, SECRET)
            .compact()
    }

    fun getUid(token: String): String? {
        try {
            return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .body
                .subject
        } catch (e: Exception) {
            return null
        }

    }

    fun resolveToken(request: HttpServletRequest): String? {
        try {
            val token = request.getHeader(TOKEN_HEADER)
            if (token != null) {
                return token
            }
            return null
        } catch (e: Exception) {
            return null
        }

    }
}