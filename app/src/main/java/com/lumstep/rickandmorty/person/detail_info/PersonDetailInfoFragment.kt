package com.lumstep.rickandmorty.person.detail_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.databinding.FragmentPersonDetailInfoBinding
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.person.Person
import javax.inject.Inject

class PersonDetailInfoFragment : Fragment() {
    private var _binding: FragmentPersonDetailInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navigator: FragmentNavigator

    @Inject
    lateinit var repository: PersonDetailRepository

    private lateinit var viewModel: PersonDetailInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPersonDetailInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireContext().appComponent.inject(this)

        initViewModel()
        initFloatActionButton()
        initPullToRefreshOption()
    }

    private fun initPullToRefreshOption() {
        binding.personDetailInfoRefresh.setOnRefreshListener {
            viewModel.updatePerson(requireArguments().getInt(PERSON_ID))
            binding.personDetailInfoRefresh.isRefreshing = false
        }
    }

    private fun initFloatActionButton() {
        binding.personDetailInfoFab.setOnClickListener {
            navigator.closeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[PersonDetailInfoViewModel::class.java]

        viewModel.initRepository(repository)
        viewModel.updatePerson(requireArguments().getInt(PERSON_ID))

        viewModel.personDetailInfo.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                showPersonInfo(it)
                viewModel.updateEpisodeList(it.episode)
            } else {
                binding.personListErrorImage.isVisible = true
                binding.personListRetryButton.isVisible = true
                binding.personListRetryButton.setOnClickListener {
                    viewModel.updatePerson(requireArguments().getInt(PERSON_ID))
                }
            }
        }

        viewModel.personEpisodeList.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                initRecyclerView(it)
                binding.personDetailInfoProgressBar.isVisible = false
            } else {
                binding.personListErrorImage.isVisible = true
                binding.personListRetryButton.isVisible = true
                binding.personListRetryButton.setOnClickListener {
                    viewModel.updateEpisodeList(viewModel.personDetailInfo.value?.episode)
                }
            }
        }
    }

    private fun initRecyclerView(episodes: List<Episode>) {
        binding.personDetailInfoRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            adapter = PersonEpisodeRecyclerViewAdapter(episodes)

        }
    }

    private fun showPersonInfo(person: Person) {
        val circularProgressDrawable = CircularProgressDrawable(requireContext())

        Log.d("MAIN", "${person.name}")

        binding.personDetailInfoGrid.visibility = View.VISIBLE
        binding.personDetailInfoName.text = person.name
        binding.personDetailInfoStatus.text = person.status
        binding.personDetailInfoSpecies.text = person.species
        binding.personDetailInfoType.text = person.type
        binding.personDetailInfoGender.text = person.gender
        binding.personDetailInfoUrl.text = person.url
        binding.personDetailInfoCreated.text = person.created

        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()

        binding.personDetailInfoImage.load(person.image) {
            crossfade(true)
            placeholder(circularProgressDrawable)
            transformations(CircleCropTransformation())
            error(R.drawable.error)
        }

        if (person.location?.url != null && person.location.url.isNotEmpty()) {
            binding.personDetailInfoLocation.text =
                HtmlCompat.fromHtml("<u>" + person.location.name + "</u> ",
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.personDetailInfoLocation.setOnClickListener {
                val path = person.location.url.split("/")
                val id = path[path.lastIndex].toInt()
                navigator.showLocationDetailInfoFragment(id)
            }
        } else binding.personDetailInfoLocation.text = person.location?.name

        if (person.origin?.url != null && person.origin.url.isNotEmpty()) {
            binding.personDetailInfoOrigin.text =
                HtmlCompat.fromHtml("<u>" + person.origin.name + "</u> ",
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.personDetailInfoOrigin.setOnClickListener {
                val path = person.origin.url.split("/")
                val id = path[path.lastIndex].toInt()
                navigator.showLocationDetailInfoFragment(id)
            }
        }else binding.personDetailInfoOrigin.text = person.origin?.name

    }

    companion object {
        const val FRAGMENT_CONTACT_TAG = "PERSON_DETAIL_INFO_FRAGMENT"
        private const val PERSON_ID = "PERSON_ID"
        fun newInstance(id: Int) = PersonDetailInfoFragment().also {
            it.arguments = Bundle().apply {
                putInt(PERSON_ID, id)
            }
        }
    }

}
