package com.lumstep.rickandmorty.person

import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.Mapper
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions

class PersonMapperTest {

    private lateinit var mapper: Mapper<Person, PersonEntity>
    private val persons = ArrayList<Person>()
    private lateinit var person: Person
    private val personEntities = ArrayList<PersonEntity>()
    private lateinit var entity: PersonEntity

    @Before
    fun setup() {
        mapper = PersonMapper

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            persons.add(Person(id = id,
                name = id.toString(),
                status = null,
                species = null,
                type = null,
                gender = null,
                origin = null,
                location = null,
                url = null,
                created = null,
                image = null,
                episode = null))
        }

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            personEntities.add(PersonEntity(id = id,
                name = id.toString(),
                status = null,
                species = null,
                type = null,
                gender = null,
                originName = null,
                originUrl = null,
                locationName = null,
                locationUrl = null,
                url = null,
                created = null,
                image = null,
                episode = null))
        }

        person = Person(id = 0,
            name = "Stepan",
            status = "Alive",
            species = null,
            type = null,
            gender = null,
            origin = Person.PersonLocation("Earth", null),
            location = Person.PersonLocation("Earth", null),
            url = null,
            created = "30.06.1999",
            image = null,
            episode = null)

        entity = PersonEntity(
            id = 0,
            name = "Stepan",
            status = "Alive",
            species = null,
            type = null,
            gender = null,
            originName = "Earth",
            originUrl = null,
            locationName = "Earth",
            locationUrl = null,
            url = null,
            created = "30.06.1999",
            image = null,
            episode = null
        )
    }

    @Test
    fun `getListEntities`() {

        val result = mapper.mapListToEntities(persons)

        Assertions.assertTrue(result == personEntities)
    }

    @Test
    fun `getListDates`() {

        val result = mapper.mapListFromEntities(personEntities)

        Assertions.assertTrue(result == persons)
    }

    @Test
    fun `getEntity`() {

        val result = mapper.mapToEntity(person)

        Assertions.assertTrue(result == entity)
    }

    @Test
    fun `getData`() {

        val result = mapper.mapFromEntity(entity)

        Assertions.assertTrue(result == person)
    }
}