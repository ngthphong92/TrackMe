package com.ngthphong92.trackme.extension

import com.ngthphong92.trackme.DEFAULT_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*


fun Long.toFormat(format: String? = DEFAULT_TIME_FORMAT): String {
    val formatter = SimpleDateFormat(format, Locale("vi"))
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return formatter.format(cal.time)
}