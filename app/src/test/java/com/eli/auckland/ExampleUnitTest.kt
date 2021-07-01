package com.eli.auckland

import org.junit.Test

import org.junit.Assert.*
import java.text.DateFormat
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
        val dateAsString = "Wednesday 14 July 2021"
        val dateTimeFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US)
        val date = dateTimeFormat.parse(dateAsString)
        println()
    }
}