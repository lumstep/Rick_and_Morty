package com.lumstep.rickandmorty.location

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationService {
    @GET("/api/location/{id}")
    suspend fun getLocation(@Path("id") id: Int): Response<Location>

    @GET("/api/location")
    suspend fun getLocationPage(@Query("page") pageNumber: Int): Response<LocationList>

    @GET("/api/location/{location_numbers}")
    suspend fun getLocationList(@Path("location_numbers") location_numbers: String): Response<List<Location>>

    @GET("/api/location/")
    suspend fun getLocationFilteredList(
        @Query("page") pageNumber: Int,
        @Query("name") name: String?,
        @Query("type") type: String?,
        @Query("dimension") dimension: String?,
    ): Response<LocationList>
}