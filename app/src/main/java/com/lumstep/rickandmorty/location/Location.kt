package com.lumstep.rickandmorty.location

data class Location(
    val id: Int,
    val name: String?,
    val type: String?,
    val dimension: String?,
    val residents: List<String>?,
    val url:String?,
    val created: String?
)
