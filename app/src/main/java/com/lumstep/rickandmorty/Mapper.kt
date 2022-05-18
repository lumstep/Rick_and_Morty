package com.lumstep.rickandmorty

interface Mapper<Data, Entity> {
    fun mapToEntity(data: Data): Entity
    fun mapListToEntities(dataList: List<Data>): List<Entity>
    fun mapFromEntity(entity: Entity): Data
    fun mapListFromEntities(entities: List<Entity>): List<Data>
}