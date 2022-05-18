package com.lumstep.rickandmorty.location.location_list

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
import com.lumstep.rickandmorty.location.Location
import javax.inject.Inject

class LocationListRecyclerViewAdapter @Inject constructor():
    PagingDataAdapter<Location, LocationListRecyclerViewAdapter.LocationViewHolder>(DiffUtilCallBack) {
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):LocationViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(inflater)
    }

    class LocationViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.location_name)
        private val type: TextView = view.findViewById(R.id.location_type)
        private val dimension: TextView = view.findViewById(R.id.location_dimension)
        private val locationLink:TableLayout = view.findViewById(R.id.location_table_layout)

        private val navigator: FragmentNavigator = itemView.context.appComponent.navigator


        fun bind(location: Location) {
            locationLink.setOnClickListener {
                navigator.showLocationDetailInfoFragment(location.id)
            }
            name.text = location.name
            type.text = location.type
            dimension.text = location.dimension
        }
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }

    }
}

