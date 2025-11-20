package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.*

/**
 * UserDao - Opérations sur la table users
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserById(uid: String): LiveData<User?>

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserByIdSync(uid: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}
