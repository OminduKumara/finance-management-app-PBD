package com.example.pbdassignment.feature.authentication.data

import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.authentication.domain.User
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: ""
                    )
                )
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("User record not found")
            Resource.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: ""
                )
            )
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Invalid email or password.")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected authentication error occurred.")
        }
    }
    override suspend fun register(email: String, password: String, displayName: String): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Failed to create user record")

            // Set display name in Firebase profile
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }
            firebaseUser.updateProfile(profileUpdates).await()
            Resource.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = displayName
                )
            )
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("An account with this email already exists.")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to complete registration.")
        }
    }
    override suspend fun logout() {
        firebaseAuth.signOut()
    }
    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}