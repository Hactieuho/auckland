package com.liz.auckland

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testDateTimeFormat() {
        val dateAsString = "Wednesday 14 July 2021 00:00:00:000 UTC"
        val dateTimeFormat = SimpleDateFormat("EEEE dd MMMM yyyy H:m:s:S Z", Locale.US)
        dateTimeFormat.timeZone = TimeZone.getTimeZone("Etc/GMT+0")
        val date = dateTimeFormat.parse(dateAsString)
        println(date.toLocaleString())
    }

    @Test
    fun testTimeZone() {
        val dateString = "Thursday 8 July 2021"
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEEE dd MMMM yyyy")
        calendar.time = sdf.parse(dateString)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = calendar.time
        println(date.toLocaleString())
    }

    @Test
    fun testCalendar() {
        val mCalendar = GregorianCalendar()
        val mTimeZone = mCalendar.timeZone
        val mGMTOffset = mTimeZone.rawOffset
        println("GMT offset is $mGMTOffset milliseconds")
        val calendar = Calendar.getInstance()
        val timeZone = calendar.timeZone
        val GMTOffset = timeZone.rawOffset
        println("GMT offset is $GMTOffset milliseconds")
    }
}