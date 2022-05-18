package com.lumstep.rickandmorty.episode.detail_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.databinding.FragmentEpisodeDetailInfoBinding
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.location.detail_info.LocationDetailInfoFragment
import com.lumstep.rickandmorty.person.Person
import javax.inject.Inject

class EpisodeDetailInfoFragment : Fragment() {
    private var _binding: FragmentEpisodeDetailInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EpisodeDetailInfoViewModel

    @Inject
    lateinit var navigator: FragmentNavigator

    @Inject
    lateinit var repository: EpisodeDetailRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEpisodeDetailInfoBinding.inflate(inflater, container, false)
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
        binding.episodeDetailInfoRefresh.setOnRefreshListener {
            viewModel.updateEpisode(requireArguments().getInt(EPISODE_ID))
            binding.episodeDetailInfoRefresh.isRefreshing = false
        }
    }

    private fun initFloatActionButton() {
        binding.episodeDetailInfoFab.setOnClickListener {
            navigator.closeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[EpisodeDetailInfoViewModel::class.java]

        viewModel.initRepository(repository)
        viewModel.updateEpisode(requireArguments().getInt(EPISODE_ID))

        viewModel.episodeDetailInfo.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                showEpisodeInfo(it)
                viewModel.updatePersonList(it.characters)
            } else {
                binding.episodeListErrorImage.isVisible = true
                binding.episodeListRetryButton.isVisible = true
                binding.episodeListRetryButton.setOnClickListener {
                    viewModel.updateEpisode(requireArguments().getInt(EPISODE_ID))
                }
            }
        }

        viewModel.episodePersonList.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                initRecyclerView(it)
                binding.episodeDetailInfoProgressBar.isVisible = false
            } else {
                binding.episodeListErrorImage.isVisible = true
                binding.episodeListRetryButton.isVisible = true
                binding.episodeListRetryButton.setOnClickListener {
                    viewModel.updatePersonList(viewModel.episodeDetailInfo.value?.characters)
                }
            }
        }
    }

    private fun initRecyclerView(persons: List<Person>) {

        binding.episodeDetailInfoRecycler.apply {
            val mLayoutManager =
                GridLayoutManager(this@EpisodeDetailInfoFragment.requireContext(), 2)
            layoutManager = mLayoutManager

            adapter = EpisodePersonRecyclerViewAdapter(persons)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                        binding.episodeDetailInfoRefresh.isEnabled = false

                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        binding.episodeDetailInfoRefresh.isEnabled = true
                }
            })
        }
    }

    private fun showEpisodeInfo(episode: Episode) {
        Log.d("MAIN", "${episode.name}")

        binding.episodeDetailInfoTable.visibility = View.VISIBLE
        binding.episodeDetailInfoName.text = episode.name
        binding.episodeDetailInfoNumber.text = episode.episode
        binding.episodeDetailInfoAirDate.text = episode.air_date
        binding.episodeDetailInfoUrl.text = episode.url
        binding.episodeDetailInfoCreated.text = episode.created
    }

    companion object {
        const val FRAGMENT_CONTACT_TAG = "EPISODE_DETAIL_INFO_FRAGMENT"
        private const val EPISODE_ID = "EPISODE_ID"
        fun newInstance(id: Int) = EpisodeDetailInfoFragment().also {
            it.arguments = Bundle().apply {
                putInt(EPISODE_ID, id)
            }
        }
    }

}