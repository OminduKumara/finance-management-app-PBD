package com.example.pbdassignment.feature.authentication.domain

import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.authentication.domain.User
import com.example.pbdassignment.feature.authentication.domain.AuthRepository

class Login(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        return authRepository.login(email.trim(), password)
    }
}