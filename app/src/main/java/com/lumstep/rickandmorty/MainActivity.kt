package com.lumstep.rickandmorty

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(applicationContext)
        appComponent = DaggerAppComponent.builder().dataModule(DataModule(database)).build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is MainApp -> appComponent
        else -> this.applicationContext.appComponent
    }

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: FragmentNavigator

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appComponent.inject(this)
        navigator.setFragmentManager(supportFragmentManager)

        initBottomNavigationView()

        navigator.showPersonListFragment()
    }

    private fun initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.personListFragment -> {
                    navigator.showPersonListFragment()
                }
                R.id.episodeListFragment -> {
                    navigator.showEpisodeListFragment()
                }
                R.id.locationListFragment -> {
                    navigator.showLocationListFragment()
                }
            }
            true
        }
    }
}
