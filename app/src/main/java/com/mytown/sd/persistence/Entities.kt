package com.mytown.sd.persistence

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mytown.reports.Report
import com.mytown.sd.R
import com.mytown.sd.extension.getCSVString
import com.mytown.sd.extension.toFormattedString
import java.security.AccessControlContext


@Entity
class User : Report {
    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "mobile_number")
    var mobileNumber: String? = null

    @ColumnInfo(name = "temperature")
    var temperature: String? = null

    @ColumnInfo(name = "address")
    var address: String? = null

    @ColumnInfo(name = "area")
    var area: String? = null

    @ColumnInfo(name = "time_stamp")
    var timeStamp: Long? = null

    @ColumnInfo(name = "temp_1")
    var temp_1: String? = null

    @ColumnInfo(name = "temp_2")
    var temp_2: String? = null

    override fun getHeader(context: Context): String {
        return "${context.getString(R.string.header_date)}," +
                "${context.getString(R.string.header_name)}," +
                "${context.getString(R.string.header_mobile)}," +
                "${context.getString(R.string.header_temperature)}," +
                "${context.getString(R.string.header_area)}," +
                "${context.getString(R.string.header_address)}"
    }

    override fun getRow(): String {
        val dateString = timeStamp!!.toFormattedString()
        return "$dateString,${name!!.getCSVString()},${mobileNumber!!.getCSVString()}," +
                "${temperature!!.getCSVString()},${area!!.getCSVString()},${address!!.getCSVString()}"
    }
}
