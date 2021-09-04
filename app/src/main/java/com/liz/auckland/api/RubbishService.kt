package com.liz.auckland.api

import com.liz.auckland.model.Rubbish
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor

interface RubbishService {
    @GET("town_citys")
    fun getTownCities() : Call<List<String?>?>?
    @GET("suburb_localities")
    fun getSuburbLocalities(@Query("town_city") townCity: String?) : Call<List<String?>?>?
    @GET("full_road_names")
    fun getRoadNames(@Query("suburb_locality") locality: String?) : Call<List<String?>?>?
    @GET("full_address_numbers")
    fun getAddressNumbers(@Query("suburb_locality") locality: String?, @Query("full_road_name") roadName: String?) : Call<List<String?>?>?
    @GET("rubbish")
    fun getRubbish(@Query("an") an: String?) : Call<Rubbish?>?
}