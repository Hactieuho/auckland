package com.eli.auckland.api

import com.eli.auckland.model.AddressResult
import com.eli.auckland.model.Rubbish
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://sinno.soict.ai:11080/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface RubbishService {
    @GET("locations")
    fun getLocations() : Call<AddressResult?>?
    @GET("rubbish")
    fun getRubbish(@Query("an") an: String?) : Call<Rubbish?>?
}

object RubbishApi {
    val api: RubbishService by lazy {
        retrofit.create(RubbishService::class.java)
    }
}