package com.lumstep.rickandmorty.location

import com.lumstep.rickandmorty.Mapper

object LocationMapper : Mapper<Location, LocationEntity> {
    override fun mapToEntity(location: Location): LocationEntity {
        val locationIdList =
            if (location.residents != null && location.residents.isNotEmpty()) {
                val s = StringBuilder()
                for (personId in location.residents) {
                    s.append(personId).append(",")
                }
                s.deleteCharAt(s.lastIndex)
                s.toString()
            } else null
        return LocationEntity(
            id = location.id,
            name = location.name,
            type = location.type,
            dimension = location.dimension,
            residents = locationIdList,
            url = location.url,
            created = location.created
        )
    }

    override fun mapFromEntity(location: LocationEntity): Location {
        val personIdList = location.residents?.split(",")

        return Location(
            id = location.id,
            name = location.name,
            type = location.type,
            dimension = location.dimension,
            residents = personIdList,
            url = location.url,
            created = location.created
        )
    }

    override fun mapListToEntities(locations: List<Location>): List<LocationEntity> {
        val locationEntityList = ArrayList<LocationEntity>()
        for (location in locations) {
            locationEntityList.add(mapToEntity(location))
        }
        return locationEntityList
    }

    override fun mapListFromEntities(locations: List<LocationEntity>): List<Location> {
        val locationList = ArrayList<Location>()
        for (location in locations) {
            locationList.add(mapFromEntity(location))
        }
        return locationList
    }

}