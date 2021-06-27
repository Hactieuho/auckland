package com.eli.auckland.constant

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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