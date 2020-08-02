package com.mytown.sd.persistence

import android.content.Context

object PreferenceManager {

    fun putArea(context: Context,area:String){
        val sharedPref = context.getSharedPreferences(
            "PREF_VISITOR", Context.MODE_PRIVATE)
        val set = sharedPref.getStringSet("area_names", mutableSetOf()) ?: mutableSetOf()
        set.add(area)
        sharedPref.edit().putStringSet("area_names",set).commit()
    }

    fun getArea(context: Context): Array<String> {
        val sharedPref = context.getSharedPreferences(
            "PREF_VISITOR", Context.MODE_PRIVATE)
       val set =  sharedPref.getStringSet("area_names", mutableSetOf()) ?: mutableSetOf()
       return set.toTypedArray()
    }

}