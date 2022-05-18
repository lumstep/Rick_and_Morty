package com.lumstep.rickandmorty.episode

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")

data class EpisodeEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val air_date: String?,
    val episode: String?,
    val characters: String?,
    val url: String?,
    val created: String?
)