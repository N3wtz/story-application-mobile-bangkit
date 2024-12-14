package com.bangkit.storyapplicationgagas.Utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.dateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = inputFormat.parse(this) as Date
    return  DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(date)
}