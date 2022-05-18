package com.lumstep.rickandmorty.person

import com.lumstep.rickandmorty.Mapper

object PersonMapper : Mapper<Person, PersonEntity> {
    override fun mapToEntity(person: Person): PersonEntity {
        val episodeIdList =
            if (person.episode != null) {
                val s = StringBuilder()
                for (episodeId in person.episode) {
                    s.append(episodeId).append(",")
                }
                s.deleteCharAt(s.lastIndex)
                s.toString()
            } else null

        return PersonEntity(
            id = person.id,
            name = person.name,
            status = person.status,
            species = person.species,
            type = person.type,
            gender = person.gender,
            originName = person.origin?.name,
            originUrl = person.origin?.url,
            locationName = person.location?.name,
            locationUrl = person.location?.url,
            image = person.image,
            episode = episodeIdList,
            url = person.url,
            created = person.created
        )
    }


    override fun mapFromEntity(person: PersonEntity): Person {
        val episodeIdList = person.episode?.split(",")

        return Person(
            id = person.id,
            name = person.name,
            status = person.status,
            species = person.species,
            type = person.type,
            gender = person.gender,
            origin = if (person.originName != null || person.originUrl != null) Person.PersonLocation(
                person.originName,
                person.originUrl) else null,
            location = if (person.locationName != null || person.locationUrl != null) Person.PersonLocation(
                person.locationName,
                person.locationUrl) else null,
            image = person.image,
            episode = episodeIdList,
            url = person.url,
            created = person.created
        )
    }

    override fun mapListToEntities(persons: List<Person>): List<PersonEntity> {
        val personEntityList = ArrayList<PersonEntity>()
        for (person in persons) {
            personEntityList.add(mapToEntity(person))
        }
        return personEntityList
    }

    override fun mapListFromEntities(persons: List<PersonEntity>): List<Person> {
        val personList = ArrayList<Person>()
        for (person in persons) {
            personList.add(mapFromEntity(person))
        }
        return personList
    }
}