package com.example.demo.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal as? UserPrincipal
            ?: throw IllegalStateException("No authenticated user found")
        return principal.id
    }
}
