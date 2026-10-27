package com.example.pbdassignment.feature.authentication.domain

import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.authentication.domain.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(email: String, password: String, displayName: String): Resource<User>
    suspend fun logout()
    fun getCurrentUserId(): String?
}