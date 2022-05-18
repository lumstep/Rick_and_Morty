package com.lumstep.rickandmorty

import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.episode.EpisodeEntity
import com.lumstep.rickandmorty.episode.EpisodeMapper
import com.lumstep.rickandmorty.episode.EpisodeService
import com.lumstep.rickandmorty.episode.detail_info.EpisodeDetailInfoFragment
import com.lumstep.rickandmorty.episode.detail_info.EpisodeDetailInfoRepository
import com.lumstep.rickandmorty.episode.detail_info.EpisodeDetailRepository
import com.lumstep.rickandmorty.episode.episode_list.EpisodeListFragment
import com.lumstep.rickandmorty.episode.episode_list.EpisodeListRepository
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.location.LocationEntity
import com.lumstep.rickandmorty.location.LocationMapper
import com.lumstep.rickandmorty.location.LocationService
import com.lumstep.rickandmorty.location.detail_info.LocationDetailInfoFragment
import com.lumstep.rickandmorty.location.detail_info.LocationDetailInfoRepository
import com.lumstep.rickandmorty.location.detail_info.LocationDetailRepository
import com.lumstep.rickandmorty.location.location_list.LocationListFragment
import com.lumstep.rickandmorty.location.location_list.LocationListRepository
import com.lumstep.rickandmorty.person.Person
import com.lumstep.rickandmorty.person.PersonEntity
import com.lumstep.rickandmorty.person.PersonMapper
import com.lumstep.rickandmorty.person.PersonService
import com.lumstep.rickandmorty.person.detail_info.PersonDetailInfoFragment
import com.lumstep.rickandmorty.person.detail_info.PersonDetailInfoRepository
import com.lumstep.rickandmorty.person.detail_info.PersonDetailRepository
import com.lumstep.rickandmorty.person.person_list.PersonListFragment
import com.lumstep.rickandmorty.person.person_list.PersonListRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class, NetworkModule::class, MapperModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(listFragment: PersonListFragment)
    fun inject(detailInfoFragment: PersonDetailInfoFragment)
    fun inject(listFragment: EpisodeListFragment)
    fun inject(detailInfoFragment: EpisodeDetailInfoFragment)
    fun inject(listFragment: LocationListFragment)
    fun inject(detailInfoFragment: LocationDetailInfoFragment)

    val navigator: FragmentNavigator
}

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideNavigator(): FragmentNavigator {
        return Navigator()
    }
}

@Module
class MapperModule {

    @Singleton
    @Provides
    fun providePersonMapper(): Mapper<Person, PersonEntity> {
        return PersonMapper
    }

    @Singleton
    @Provides
    fun provideEpisodeMapper(): Mapper<Episode, EpisodeEntity> {
        return EpisodeMapper
    }

    @Singleton
    @Provides
    fun provideLocationMapper(): Mapper<Location, LocationEntity> {
        return LocationMapper
    }
}

@Module
class DataModule(val database: AppDatabase) {

    @Singleton
    @Provides
    fun provideDatabase(): AppDatabase {
        return database
    }

    @Singleton
    @Provides
    fun provideListRepositoryPerson(
        database: AppDatabase,
        personService: PersonService,
        personMapper: Mapper<Person, PersonEntity>
    ): ListRepository<Person> {
        return PersonListRepository(database, personService, personMapper)
    }

    @Singleton
    @Provides
    fun provideDetailInfoRepositoryPerson(
        database: AppDatabase,
        personService: PersonService,
        episodeService: EpisodeService,
        personMapper: Mapper<Person, PersonEntity>,
        episodeMapper: Mapper<Episode, EpisodeEntity>
    ): PersonDetailRepository {
        return PersonDetailInfoRepository(
            database,
            personService,
            episodeService,
            personMapper,
            episodeMapper
        )
    }

    @Singleton
    @Provides
    fun provideListRepositoryEpisode(
        database: AppDatabase,
        episodeService: EpisodeService,
        episodeMapper: Mapper<Episode, EpisodeEntity>
    ): ListRepository<Episode> {
        return EpisodeListRepository(database, episodeService, episodeMapper)
    }

    @Singleton
    @Provides
    fun provideDetailInfoRepositoryEpisode(
        database: AppDatabase,
        personService: PersonService,
        episodeService: EpisodeService,
        episodeMapper: Mapper<Episode, EpisodeEntity>,
        personMapper: Mapper<Person, PersonEntity>
    ): EpisodeDetailRepository {
        return EpisodeDetailInfoRepository(
            database,
            personService,
            episodeService,
            personMapper,
            episodeMapper
        )
    }

    @Singleton
    @Provides
    fun provideListRepositoryLocation(
        database: AppDatabase,
        locationService: LocationService,
        locationMapper: Mapper<Location, LocationEntity>
    ): ListRepository<Location> {
        return LocationListRepository(database, locationService, locationMapper)
    }

    @Singleton
    @Provides
    fun provideDetailInfoRepositoryLocation(
        database: AppDatabase,
        personService: PersonService,
        locationService: LocationService,
        locationMapper: Mapper<Location, LocationEntity>,
        personMapper: Mapper<Person, PersonEntity>
    ): LocationDetailRepository {
        return LocationDetailInfoRepository(
            database,
            locationService,
            personService,
            locationMapper,
            personMapper
        )
    }
}

@Module
class NetworkModule {

    private fun retrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()


        return Retrofit.Builder().baseUrl("https://rickandmortyapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providePersonApi(): PersonService {
        return retrofit().create(PersonService::class.java)
    }

    @Provides
    fun provideEpisodeApi(): EpisodeService {
        return retrofit().create(EpisodeService::class.java)
    }

    @Provides
    fun provideLocationApi(): LocationService {
        return retrofit().create(LocationService::class.java)
    }

}
