package com.lumstep.rickandmorty.location

import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.Mapper
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions

class LocationMapperTest {

    private lateinit var mapper: Mapper<Location, LocationEntity>
    private val locations = ArrayList<Location>()
    private lateinit var location: Location
    private val locationEntities = ArrayList<LocationEntity>()
    private lateinit var entity: LocationEntity

    @Before
    fun setup() {
        mapper = LocationMapper

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            locations.add(Location(id = id,
                name = id.toString(),
                type = null,
                dimension = null,
                url = null,
                created = null,
                residents = null))
        }

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            locationEntities.add(LocationEntity(id = id,
                name = id.toString(),
                type = null,
                dimension = null,
                url = null,
                created = null,
                residents = null))
        }

        location = Location(id = 0,
            name = "Earth",
            type = "Planet",
            dimension = null,
            url = null,
            created = null,
            residents = null)

        entity = LocationEntity(
            id = 0,
            name = "Earth",
            type = "Planet",
            dimension = null,
            url = null,
            created = null,
            residents = null
        )
    }

    @Test
    fun `getListEntities`() {

        val result = mapper.mapListToEntities(locations)

        Assertions.assertTrue(result == locationEntities)
    }

    @Test
    fun `getListDates`() {

        val result = mapper.mapListFromEntities(locationEntities)

        Assertions.assertTrue(result == locations)
    }

    @Test
    fun `getEntity`() {

        val result = mapper.mapToEntity(location)

        Assertions.assertTrue(result == entity)
    }

    @Test
    fun `getData`() {

        val result = mapper.mapFromEntity(entity)

        Assertions.assertTrue(result == location)
    }
}