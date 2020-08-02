/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mytown.sd.entry

import android.database.Observable
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import com.mytown.sd.persistence.Suggestion
import com.mytown.sd.persistence.SuggestionDao
import com.mytown.sd.persistence.User
import com.mytown.sd.persistence.UserDao

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class UserRepository(private val userDao: UserDao,private val suggestionDao: SuggestionDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allUsers: LiveData<List<User>> = userDao.all


    var suggestion: ObservableArrayList<Suggestion> = ObservableArrayList<Suggestion>()

    // You must call this on a non-UI thread or your app will crash. So we're making this a
    // suspend function so the caller methods know this.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        user.timeStamp = System.currentTimeMillis()
        userDao.insert(user)
        insertSuggestion(user)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSuggestion(mobileNumber: String)  {
        suggestion.clear()
        suggestion.addAll(suggestionDao.loadByMobile(mobileNumber))
        Log.i("TAG","Suggestions")
    }


    private fun insertSuggestion(user: User){
        val suggestion = Suggestion()
        suggestion.name = user.name
        suggestion.mobileNumber = user.mobileNumber
        suggestion.area = user.area
        suggestion.address = user.address
        suggestionDao.insert(suggestion)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun deleteRecords() {
        userDao.delete()
    }
}
