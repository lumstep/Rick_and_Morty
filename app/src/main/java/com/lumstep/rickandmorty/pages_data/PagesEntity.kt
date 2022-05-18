package com.lumstep.rickandmorty.pages_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
class PagesEntity(
    @PrimaryKey
    val item: String,
    var page: Int,
)