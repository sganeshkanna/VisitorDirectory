package com.mytown.sd.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @get:Query("SELECT * FROM user")
    val all: LiveData<List<User>>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray?): List<User?>?

    @Query("SELECT * FROM user LIMIT (:limit) OFFSET (:offset)")
    fun loadUser(limit: Int, offset: Int): List<User?>?

    @Insert
    fun insert(vararg users: User?)

    @Query("DELETE FROM User")
    fun delete()
}
