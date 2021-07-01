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
        dateTimeFormat1 = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
        dateTimeFormat2 = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US)
    }

    override fun fromJson(reader: JsonReader): Date? {
        val dateAsString = reader.nextString()
        var result: Date? = null
        try {
            result = synchronized(dateTimeFormat1) {
                dateTimeFormat1.parse(dateAsString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (result == null) {
            try {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                result = synchronized(dateTimeFormat2) {
                    dateTimeFormat2.parse("$dateAsString $year")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateTimeFormat1) {
                writer.value(value.toString())
            }
        }
    }
}