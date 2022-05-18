package com.lumstep.rickandmorty

import androidx.fragment.app.FragmentManager

interface FragmentNavigator {

    fun setFragmentManager(fragmentManager: FragmentManager)
    fun showPersonListFragment()
    fun showEpisodeListFragment()
    fun showLocationListFragment()
    fun showPersonDetailInfoFragment(id : Int)
    fun showEpisodeDetailInfoFragment(id : Int)
    fun showLocationDetailInfoFragment(id : Int)

    fun closeFragment()
}