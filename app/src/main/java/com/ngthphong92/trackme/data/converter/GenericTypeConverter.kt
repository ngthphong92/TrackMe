package com.ngthphong92.trackme.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ngthphong92.trackme.data.model.Session

class GenericTypeConverter {
    @TypeConverter
    fun listSessionToJson(value: ArrayList<Session?>?): String = customGson.toJson(value)

    @TypeConverter
    fun jsonToListSession(value: String): ArrayList<Session?>? =
        customGson.fromJson(value, object : TypeToken<ArrayList<Session>>() {}.type)
}

val customGson: Gson = GsonBuilder().apply { excludeFieldsWithoutExposeAnnotation() }.create()