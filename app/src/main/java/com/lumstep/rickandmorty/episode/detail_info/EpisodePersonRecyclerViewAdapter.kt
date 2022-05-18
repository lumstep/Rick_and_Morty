package com.lumstep.rickandmorty.episode.detail_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.Navigator
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.person.Person

class EpisodePersonRecyclerViewAdapter(private val persons: List<Person>) :
    RecyclerView.Adapter<EpisodePersonRecyclerViewAdapter.EpisodePersonRecyclerViewHolder>() {
    class EpisodePersonRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.person_name)
        private val species: TextView = itemView.findViewById(R.id.person_species)
        private val status: TextView = itemView.findViewById(R.id.person_status)
        private val gender: TextView = itemView.findViewById(R.id.person_gender)
        private val personLink: TableLayout = itemView.findViewById(R.id.person_table_layout)
        private val image: ImageView = itemView.findViewById(R.id.person_image)

        private val circularProgressDrawable = CircularProgressDrawable(itemView.context)
        private val navigator: FragmentNavigator = itemView.context.appComponent.navigator

        fun bind(person: Person) {
            personLink.setOnClickListener {
                navigator.showPersonDetailInfoFragment(person.id!!)
            }
            name.text = person.name
            status.text = person.status
            species.text = person.species
            gender.text = person.gender
            circularProgressDrawable.strokeWidth = 10f
            circularProgressDrawable.centerRadius = 50f
            circularProgressDrawable.start()
            image.load(person.image) {
                crossfade(true)
                placeholder(circularProgressDrawable)
                transformations(CircleCropTransformation())
                error(R.drawable.error)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodePersonRecyclerViewAdapter.EpisodePersonRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)
        return EpisodePersonRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EpisodePersonRecyclerViewAdapter.EpisodePersonRecyclerViewHolder,
        position: Int
    ) {
        holder.bind(persons[position])
    }

    override fun getItemCount(): Int {
        return persons.size
    }

}
