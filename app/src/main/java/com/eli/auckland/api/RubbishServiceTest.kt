package com.eli.auckland.api

import com.eli.auckland.model.Rubbish
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://raw.githubusercontent.com/Hactieuho/auckland/main/app/src/main/assets/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RubbishServiceTest {
    @GET("rubbish.json")
    fun getRubbish() : Call<Rubbish?>?
}

object RubbishApiTest {
    val api: RubbishServiceTest by lazy {
        retrofit.create(RubbishServiceTest::class.java)
    }
}
