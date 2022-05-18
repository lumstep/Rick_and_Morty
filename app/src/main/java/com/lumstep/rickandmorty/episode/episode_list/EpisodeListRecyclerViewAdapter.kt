package com.lumstep.rickandmorty.episode.episode_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.episode.Episode
import javax.inject.Inject

class EpisodeListRecyclerViewAdapter @Inject constructor() :
    PagingDataAdapter<Episode, EpisodeListRecyclerViewAdapter.EpisodeViewHolder>(DiffUtilCallBack) {
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.episode_item, parent, false)
        return EpisodeViewHolder(inflater)
    }

    class EpisodeViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.episode_name)
        private val episodeNumber: TextView = view.findViewById(R.id.episode_number)
        private val airDate: TextView = view.findViewById(R.id.episode_air_date)
        private val episodeLink: TableLayout = view.findViewById(R.id.episode_table_layout)

        private val navigator: FragmentNavigator = itemView.context.appComponent.navigator


        fun bind(episode: Episode) {
            episodeLink.setOnClickListener {
                navigator.showEpisodeDetailInfoFragment(episode.id)
            }
            name.text = episode.name
            episodeNumber.text = episode.episode
            airDate.text = episode.air_date

        }


    }
    object DiffUtilCallBack : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem == newItem
        }

    }
}

