package com.lumstep.rickandmorty.person

data class Person(
    val id: Int,
    val name: String?,
    val status: String?,
    val species: String?,
    val type: String?,
    val gender: String?,
    val origin: PersonLocation?,
    val location: PersonLocation?,
    val image: String?,
    val episode: List<String>?,
    val url: String?,
    val created: String?
) {
    data class PersonLocation(val name: String?, val url: String?)
}
