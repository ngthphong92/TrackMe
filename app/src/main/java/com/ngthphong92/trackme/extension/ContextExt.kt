package com.ngthphong92.trackme.extension

import android.content.Context
import com.ngthphong92.trackme.TRACK_ME_SHARE_PREF
import com.ngthphong92.trackme.data.converter.customGson


fun Context.writeToSharePref(key: String, value: Any?) {
    val sharedPref = this.getSharedPreferences(TRACK_ME_SHARE_PREF, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        when (value) {
            is Int -> putInt(key, value)
            is String -> putString(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            else -> putString(key, customGson.toJson(value))
        }
        apply()
    }
}

fun Context.readFromSharePref(key: String, value: Any? = null): Any? {
    val sharedPref = this.getSharedPreferences(TRACK_ME_SHARE_PREF, Context.MODE_PRIVATE) ?: return null
    return when (value) {
        is Int -> sharedPref.getInt(key, 0)
        is String -> sharedPref.getString(key, "")
        is Long -> sharedPref.getLong(key, 0L)
        is Float -> sharedPref.getFloat(key, 0f)
        is Boolean -> sharedPref.getBoolean(key, false)
        else -> sharedPref.getString(key, "")

    }
}

fun Context.removeFromSharePref(key: String) {
    val sharedPref = this.getSharedPreferences(TRACK_ME_SHARE_PREF, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        remove(key)
        commit()
    }
}