package com.liz.auckland.api

import com.liz.auckland.model.Rubbish
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RubbishService {
    @GET("town_citys")
    suspend fun getTownCities() : Response<List<String?>?>
    @GET("suburb_localities")
    suspend fun getSuburbLocalities(@Query("town_city") townCity: String?) : Response<List<String?>?>
    @GET("full_road_names")
    suspend fun getRoadNames(@Query("suburb_locality") locality: String?) : Response<List<String?>?>
    @GET("full_address_numbers")
    suspend fun getAddressNumbers(@Query("suburb_locality") locality: String?, @Query("full_road_name") roadName: String?) : Response<List<String?>?>
    @GET("rubbish")
    suspend fun getRubbish(@Query("an") an: String?) : Response<Rubbish?>
}