package com.example.hydrationtime.data.repository

import androidx.lifecycle.LiveData
import com.example.hydrationtime.data.local.dao.*
import com.example.hydrationtime.data.local.entities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository - Gestion de l'authentification Firebase
 */
class AuthRepository(private val userDao: UserDao) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signUp(email: String, password: String, name: String, birthday: Long): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User creation failed")

            // Créer l'utilisateur dans Room
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                birthday = birthday
            )
            userDao.insertUser(user)

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Sign in failed")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUserData(uid: String): LiveData<User?> {
        return userDao.getUserById(uid)
    }
}