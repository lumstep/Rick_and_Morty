package com.lumstep.rickandmorty.person

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonService {
    @GET("/api/character/{id}")
    suspend fun getPerson(@Path("id") id: Int): Response<Person>

    @GET("/api/character")
    suspend fun getPersonPage(@Query("page") pageNumber: Int): Response<PersonList>

    @GET("/api/character/{person_numbers}")
    suspend fun getPersonList(@Path("person_numbers") person_numbers: String): Response<List<Person>>

    @GET("/api/character/")
    suspend fun getPersonFilteredList(
        @Query("page") pageNumber: Int,
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("species") species: String?,
        @Query("type") type: String?,
        @Query("gender") gender: String?,
    ): Response<PersonList>
}