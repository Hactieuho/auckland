package com.eli.auckland.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MoshiUTCDateAdapter : JsonAdapter<Date>() {
    private val dateTimeFormat1: DateFormat
    private val dateTimeFormat2: DateFormat

    init {
        dateTimeFormat1 = SimpleDateFormat("yyyyMMdd HHmmss", Locale.getDefault())
        dateTimeFormat1.timeZone = TimeZone.getTimeZone("UTC")
        dateTimeFormat2 = SimpleDateFormat("E d M", Locale.getDefault())
        dateTimeFormat2.timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun fromJson(reader: JsonReader): Date? {
        return try {
            val dateAsString = reader.nextString()
            synchronized(dateTimeFormat1) {
                dateTimeFormat1.parse(dateAsString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateTimeFormat1) {
                writer.value(value.toString())
            }
        }
    }
}