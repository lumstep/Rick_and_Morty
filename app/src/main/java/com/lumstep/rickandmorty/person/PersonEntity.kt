package com.lumstep.rickandmorty.person

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lumstep.rickandmorty.location.LocationEntity

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val status: String?,
    val species: String?,
    val type: String?,
    val gender: String?,
    val originName: String?,
    val originUrl: String?,
    val locationName: String?,
    val locationUrl: String?,
    val image: String?,
    val episode: String?,
    val url: String?,
    val created: String?,
)