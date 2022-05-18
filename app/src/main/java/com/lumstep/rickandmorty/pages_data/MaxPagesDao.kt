package com.lumstep.rickandmorty.pages_data

import androidx.room.*

@Dao
interface MaxPagesDao {

    @Query("SELECT * FROM pages WHERE item = :item")
    suspend fun getPagesEntity(item: String): PagesEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaxPages(pages: PagesEntity)

}