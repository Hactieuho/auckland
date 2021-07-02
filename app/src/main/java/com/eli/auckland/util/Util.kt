package com.eli.auckland.util

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

inline fun <reified T> SharedPreferences.saveItem(key: String, item: T?) {
    val json = Gson().toJson(item)
    edit { putString(key, json) }
}

inline fun <reified T> SharedPreferences.getItem(key: String): T? {
    val json = getString(key, "")
    if (!json.isNullOrBlank()) {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }
    return null
}

inline fun <reified T> SharedPreferences.getList(spListKey: String): ArrayList<T>? {
    val listJson = getString(spListKey, "")
    if (!listJson.isNullOrBlank()) {
        val type = object : TypeToken<ArrayList<T>>() {}.type
        return Gson().fromJson(listJson, type)
    }
    return null
}

fun Date.formatDate() : String {
    val dateTimeFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+00")
    return dateTimeFormat.format(this)
}

fun Date.formatTime() : String {
    val dateTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+00")
    return dateTimeFormat.format(this)
}