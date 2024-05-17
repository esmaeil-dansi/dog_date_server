package com.example.dog_datting.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import javax.security.auth.Subject

class AppAuthenticationToken(
    private val principal: Any,
    private var credentials: Any?,
    authorities: Collection<GrantedAuthority?>?
) :
    AbstractAuthenticationToken(authorities) {
    override fun implies(subject: Subject): Boolean {
        return false
    }

    init {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any {
        return this.credentials!!
    }

    override fun getPrincipal(): Any {
        return this.principal
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        require(!isAuthenticated) { "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead" }
        super.setAuthenticated(false)
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
//        this.credentials = null
    }

    companion object {
        private const val serialVersionUID = 530L
    }
}
