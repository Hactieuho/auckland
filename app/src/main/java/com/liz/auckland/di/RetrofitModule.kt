package com.liz.auckland.di

import com.liz.auckland.api.MoshiUTCDateAdapter
import com.liz.auckland.api.NullOnEmptyConverterFactory
import com.liz.auckland.api.RubbishService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

const val BASE_URL = "http://sinno.soict.ai:11080/"

@Qualifier
annotation class SimpleDateFormat1
@Qualifier
annotation class SimpleDateFormat2
@Qualifier
annotation class UTCTimeZone

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
    @Provides
    @Singleton
    fun provideRubbishService(moshi: Moshi, httpClient: OkHttpClient.Builder) : RubbishService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(NullOnEmptyConverterFactory())
            .client(httpClient.build())
            .build()
        return retrofit.create(RubbishService::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi(moshiUTCDateAdapter: MoshiUTCDateAdapter): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, moshiUTCDateAdapter)
            .build()

    @Provides
    @Singleton
    fun provideMoshiUTCDateAdapter(
        @SimpleDateFormat1 simpleDateFormat1: DateFormat,
        @SimpleDateFormat2 simpleDateFormat2: DateFormat
    ) : MoshiUTCDateAdapter {
        return MoshiUTCDateAdapter(simpleDateFormat1, simpleDateFormat2)
    }

    @Provides
    @Singleton
    fun provideOkHttpClientBuilder() : OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return httpClient
    }

    @SimpleDateFormat1
    @Provides
    @Singleton
    fun provideSimpleDateFormat1(@UTCTimeZone timeZone: TimeZone): SimpleDateFormat {
        val dateFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
        dateFormat.timeZone = timeZone
        return dateFormat
    }

    @SimpleDateFormat2
    @Provides
    @Singleton
    fun provideSimpleDateFormat2(@UTCTimeZone timeZone: TimeZone): SimpleDateFormat {
        val dateFormat = SimpleDateFormat("yyyyMMdd HHmmss", Locale.US)
        dateFormat.timeZone = timeZone
        return dateFormat
    }

    @Provides
    @Singleton
    @UTCTimeZone
    fun provideUTCTimeZone(): TimeZone = TimeZone.getTimeZone("UTC")
}