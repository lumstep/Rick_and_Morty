package com.lumstep.rickandmorty.episode

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpisodeService {
    @GET("/api/episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): Response<Episode>

    @GET("/api/episode")
    suspend fun getEpisodePage(@Query("page") pageNumber: Int): Response<EpisodeList>

    @GET("/api/episode/{episode_numbers}")
    suspend fun getEpisodeList(@Path("episode_numbers") episode_numbers: String): Response<List<Episode>>

    @GET("/api/episode/")
    suspend fun getEpisodeFilteredList(
        @Query("page") pageNumber: Int,
        @Query("name") name: String?,
        @Query("episode") episode: String?,
    ): Response<EpisodeList>
}