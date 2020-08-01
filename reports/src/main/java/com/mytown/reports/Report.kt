package com.mytown.reports

import android.content.Context

interface Report {
    fun getHeader(context:Context): String
    fun getRow(): String
}