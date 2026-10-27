package com.example.pbdassignment.feature.authentication.domain

import com.example.pbdassignment.core.common.Resource


class Register(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, displayName: String): Resource<User> {
        return authRepository.register(email.trim(), password, displayName.trim())
    }
}