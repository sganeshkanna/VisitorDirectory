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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mytown.sd.R
import com.mytown.sd.extension.toFormattedString
import com.mytown.sd.persistence.User


class UserListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var users = emptyList<User>() // Cached copy of words

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameText)
        val mobileTextView: TextView = itemView.findViewById(R.id.mobileText)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureText)
        val dateTextView: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val context =holder.temperatureTextView.context
        val current = users[position]
        holder.nameTextView.text = current.name
        holder.mobileTextView.text = current.mobileNumber
        holder.dateTextView.text = current.timeStamp!!.toFormattedString()
        if(current.temperature ==context.getString(R.string.empty)){
            holder.temperatureTextView.text = "${current.temperature}"
        }else {
            holder.temperatureTextView.text =
                "${current.temperature}${holder.temperatureTextView.context.getString(R.string.degree)}"
        }
    }

    internal fun setUsers(words: List<User>) {
        this.users = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = users.size

}


