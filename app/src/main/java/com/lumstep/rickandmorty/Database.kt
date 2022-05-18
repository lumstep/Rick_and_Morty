package com.lumstep.rickandmorty

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lumstep.rickandmorty.episode.EpisodeDao
import com.lumstep.rickandmorty.episode.EpisodeEntity
import com.lumstep.rickandmorty.location.LocationDao
import com.lumstep.rickandmorty.location.LocationEntity
import com.lumstep.rickandmorty.pages_data.MaxPagesDao
import com.lumstep.rickandmorty.pages_data.PagesEntity
import com.lumstep.rickandmorty.person.PersonDao
import com.lumstep.rickandmorty.person.PersonEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [PersonEntity::class, EpisodeEntity::class, LocationEntity::class, PagesEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPersonDao(): PersonDao
    abstract fun getEpisodeDao(): EpisodeDao
    abstract fun getLocationDao(): LocationDao
    abstract fun getMaxPagesDao(): MaxPagesDao

    companion object {

        const val APP_DB = "app_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, APP_DB)
                .build()
    }
}