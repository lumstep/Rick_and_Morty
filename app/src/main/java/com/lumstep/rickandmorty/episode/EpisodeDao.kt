package com.lumstep.rickandmorty.episode

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(episodeList: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Query("SELECT * FROM episodes WHERE name LIKE '%' || :name || '%' AND episode LIKE '%' || :episode_number || '%' ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getFilteredEpisodes(
        name: String?,
        episode_number: String?,
        start: Int,
        pageSize: Int
    ): List<EpisodeEntity>?

    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisode(episodeId: Int): EpisodeEntity?

    @Query("SELECT * FROM episodes ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getEpisodes(pageSize: Int, start: Int): List<EpisodeEntity>?

    @Query("SELECT * FROM episodes WHERE id IN (:listOfPersonsId)")
    suspend fun getEpisodes(listOfPersonsId: List<Int>): List<EpisodeEntity>?
}