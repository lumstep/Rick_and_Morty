package com.lumstep.rickandmorty

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lumstep.rickandmorty.episode.detail_info.EpisodeDetailInfoFragment
import com.lumstep.rickandmorty.episode.episode_list.EpisodeListFragment
import com.lumstep.rickandmorty.location.detail_info.LocationDetailInfoFragment
import com.lumstep.rickandmorty.location.location_list.LocationListFragment
import com.lumstep.rickandmorty.person.detail_info.PersonDetailInfoFragment
import com.lumstep.rickandmorty.person.person_list.PersonListFragment

class Navigator : FragmentNavigator {
    private lateinit var supportFragmentManager: FragmentManager

    override fun setFragmentManager(fragmentManager: FragmentManager) {
        supportFragmentManager = fragmentManager
    }

    override fun showPersonListFragment() {
        clearHistory()
        showFragment(
            PersonListFragment.newInstance(),
            PersonListFragment.FRAGMENT_CONTACT_TAG,
            false
        )
    }

    override fun showEpisodeListFragment() {
        clearHistory()
        showFragment(
            EpisodeListFragment.newInstance(),
            EpisodeListFragment.FRAGMENT_CONTACT_TAG,
            false
        )
    }

    override fun showLocationListFragment() {
        clearHistory()
        showFragment(
            LocationListFragment.newInstance(),
            LocationListFragment.FRAGMENT_CONTACT_TAG,
            false
        )
    }

    override fun showPersonDetailInfoFragment(id: Int) {
        showFragment(
            PersonDetailInfoFragment.newInstance(id),
            PersonDetailInfoFragment.FRAGMENT_CONTACT_TAG, true
        )
    }

    override fun showEpisodeDetailInfoFragment(id: Int) {
        showFragment(
            EpisodeDetailInfoFragment.newInstance(id),
            EpisodeDetailInfoFragment.FRAGMENT_CONTACT_TAG, true
        )
    }

    override fun showLocationDetailInfoFragment(id: Int) {
         showFragment(
             LocationDetailInfoFragment.newInstance(id),
             LocationDetailInfoFragment.FRAGMENT_CONTACT_TAG, true
        )
    }

    override fun closeFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun clearHistory() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragment, fragment, tag)

            if (addToBackStack) addToBackStack(tag)
            commit()
        }
    }

}