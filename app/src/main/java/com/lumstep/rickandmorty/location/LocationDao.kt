package com.lumstep.rickandmorty.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumstep.rickandmorty.episode.EpisodeEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locationList: List<LocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocation(locationId: Int): LocationEntity?

    @Query("SELECT * FROM locations WHERE name LIKE '%' || :name || '%' AND type LIKE '%' || :type || '%' AND dimension LIKE '%' || :dimension || '%' ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getFilteredLocations(
        name: String,
        type: String,
        dimension: String,
        start: Int,
        pageSize: Int
    ): List<LocationEntity>?

    @Query("SELECT * FROM locations ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getLocations(pageSize: Int, start: Int): List<LocationEntity>?

    @Query("SELECT * FROM locations WHERE id IN (:listOfLocationsId)")
    suspend fun getLocations(listOfLocationsId: List<Int>): List<LocationEntity>?
}