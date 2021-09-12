package com.liz.auckland.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class SimpleDateFormat1
@Qualifier
annotation class SimpleDateFormat2
@Qualifier
annotation class UTCTimeZone

@InstallIn(SingletonComponent::class)
@Module
object DateTimeModule {
    @SimpleDateFormat1
    @Singleton
    @Provides
    fun provideSimpleDateFormat1(@UTCTimeZone timeZone: TimeZone): DateFormat {
        val dateFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
        dateFormat.timeZone = timeZone
        return dateFormat
    }

    @SimpleDateFormat2
    @Singleton
    @Provides
    fun provideSimpleDateFormat2(@UTCTimeZone timeZone: TimeZone): DateFormat {
        val dateFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
        dateFormat.timeZone = timeZone
        return dateFormat
    }

    @UTCTimeZone
    @Singleton
    @Provides
    fun provideUTCTimeZone(): TimeZone = TimeZone.getTimeZone("UTC")
}