package com.mytown.sd.extension

import java.text.SimpleDateFormat
import java.util.*

val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a")
fun Long.toFormattedString(): String {
    return formatter.format(Date(this))
}

fun String.getCSVString(): String {
    return this.replace(",", "&#44;").replace("\n", "")
}
