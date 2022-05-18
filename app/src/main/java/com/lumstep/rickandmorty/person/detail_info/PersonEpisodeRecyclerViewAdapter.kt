package com.lumstep.rickandmorty.person.detail_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.databinding.PersonEpisodeItemBinding
import com.lumstep.rickandmorty.episode.Episode

class PersonEpisodeRecyclerViewAdapter(private val episodes: List<Episode>) :
    RecyclerView.Adapter<PersonEpisodeRecyclerViewAdapter.PersonEpisodeRecyclerViewHolder>() {

    class PersonEpisodeRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = PersonEpisodeItemBinding.bind(itemView)
        private val navigator: FragmentNavigator = itemView.context.appComponent.navigator

        fun bind(episode: Episode) {
            with(binding) {
                personEpisode.setOnClickListener {
                    navigator.showEpisodeDetailInfoFragment(episode.id)
                }
                personEpisodeName.text = episode.name
                personEpisodeNumber.text = episode.episode
                personEpisodeAirDate.text = episode.air_date
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonEpisodeRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.person_episode_item, parent, false)
        return PersonEpisodeRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PersonEpisodeRecyclerViewHolder,
        position: Int
    ) {
        holder.bind(episodes[position])
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

}
