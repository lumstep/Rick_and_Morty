package com.lumstep.rickandmorty.person.person_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.person.Person
import javax.inject.Inject

class PersonListRecyclerViewAdapter @Inject constructor():
    PagingDataAdapter<Person, PersonListRecyclerViewAdapter.PersonViewHolder>(DiffUtilCallBack) {
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)
        return PersonViewHolder(inflater)
    }

    class PersonViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.person_name)
        private val species: TextView = view.findViewById(R.id.person_species)
        private val status: TextView = view.findViewById(R.id.person_status)
        private val gender: TextView = view.findViewById(R.id.person_gender)
        private val personLink:TableLayout = view.findViewById(R.id.person_table_layout)
        private val image: ImageView = view.findViewById(R.id.person_image)
        private val circularProgressDrawable = CircularProgressDrawable(view.context)
        private val navigator: FragmentNavigator = itemView.context.appComponent.navigator


        fun bind(person: Person) {
            personLink.setOnClickListener {
                navigator.showPersonDetailInfoFragment(person.id)
            }
            name.text = person.name
            status.text = person.status
            species.text = person.species
            gender.text = person.gender

            circularProgressDrawable.strokeWidth = 10f
            circularProgressDrawable.centerRadius = 50f
            circularProgressDrawable.start()
            image.load(person.image){
                crossfade(true)
                placeholder(circularProgressDrawable)
                transformations(CircleCropTransformation())
                error(R.drawable.error)
            }
        }
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem == newItem
        }

    }
}

