package com.lumstep.rickandmorty.person

import androidx.room.*
import com.lumstep.rickandmorty.episode.EpisodeEntity

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personList: List<PersonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity)

    @Transaction
    @Query("SELECT * FROM persons WHERE id = :personId")
    suspend fun getPerson(personId: Int): PersonEntity?

    @Transaction
    @Query("SELECT * FROM persons WHERE name LIKE '%' || :name || '%' AND species LIKE '%' || :species || '%' AND type LIKE '%' || :type || '%' AND gender IN (:gender) AND status IN (:status) ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getFilteredPersons(
        name: String,
        status: List<String>,
        species: String,
        type: String,
        gender: List<String>,
        start: Int,
        pageSize: Int
    ): List<PersonEntity>?

    @Transaction
    @Query("SELECT * FROM persons ORDER BY id LIMIT :pageSize OFFSET :start")
    suspend fun getPersons(pageSize: Int, start: Int): List<PersonEntity>?

    @Transaction
    @Query("SELECT * FROM persons WHERE id IN (:listOfPersonsId)")
    suspend fun getPersons(listOfPersonsId: List<Int>): List<PersonEntity>?
}