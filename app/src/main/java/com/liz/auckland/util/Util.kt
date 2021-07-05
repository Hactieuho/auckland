package com.liz.auckland.util

import android.content.SharedPreferences
import android.view.MotionEvent
import android.widget.Button
import androidx.core.content.edit
import androidx.databinding.BindingAdapter
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*


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

fun Date.formatDate() : String? {
    val dateTimeFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+00")
    return dateTimeFormat.format(this)
}

fun Date.formatTime() : String? {
    val dateTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+00")
    return dateTimeFormat.format(this)
}

fun Date.formatKey() : String? {
    val dateTimeFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+00")
    return dateTimeFormat.format(this)
}

fun Date.formatRequestCode() = (this.time/1000).toInt()

@BindingAdapter("app:onClick", "app:key")
fun Button.onClick(check: (String?) -> Unit, key: String?) {
    setOnClickListener { check(key) }
}