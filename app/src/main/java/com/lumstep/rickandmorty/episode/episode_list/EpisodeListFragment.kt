package com.lumstep.rickandmorty.episode.episode_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.LoadStateAdapterForRecycler
import com.lumstep.rickandmorty.R
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.databinding.FragmentEpisodeListBinding
import com.lumstep.rickandmorty.episode.Episode
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class EpisodeListFragment : Fragment() {

    private var _binding: FragmentEpisodeListBinding? = null
    private val binding get() = _binding!!

    private var searchMenuIsVisible: Boolean = false

    private var filters = HashMap<String, String>()

    @Inject
    lateinit var recycler: EpisodeListRecyclerViewAdapter

    @Inject
    lateinit var repository: ListRepository<Episode>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEpisodeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        requireContext().appComponent.inject(this)

        initSearchMenu()
        initRecyclerView()
        initViewModel()
    }

    private fun initSearchMenu() {

        val searchButton: ImageButton = requireView().findViewById(R.id.search_icon)
        val returnButton: ImageButton =
            requireView().findViewById(R.id.episode_list_search_back_button)

        searchButton.setOnClickListener {
            toggleToolBarState()
        }

        returnButton.setOnClickListener {
            toggleToolBarState()
        }

        initFilterMenu()
    }

    private fun initFilterMenu() {

        val queueName: EditText =
            requireView().findViewById(R.id.episode_list_filter_edit_text_name)
        val queueSeason: EditText =
            requireView().findViewById(R.id.episode_list_filter_edit_text_season_number)
        val queueEpisode: EditText =
            requireView().findViewById(R.id.episode_list_filter_edit_text_episode_number)

        val searchButton: AppCompatButton =
            requireView().findViewById(R.id.episode_list_apply_filters_button)

        searchButton.setOnClickListener {
            filters.clear()

            var episodeNumber = ""

            if (queueName.text.isNotEmpty()) filters["name"] = queueName.text.toString()
            if (queueSeason.text.isNotEmpty()) episodeNumber = episodeNumber +
                    "S" + queueSeason.text.toString()
            if (queueEpisode.text.isNotEmpty()) episodeNumber = episodeNumber +
                    "E" + queueEpisode.text.toString()

            if (episodeNumber.isNotEmpty()) filters["episode_number"] = episodeNumber

            val im =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(requireView().windowToken, 0)

            recycler.refresh()

            toggleToolBarState()
        }

        binding.episodeListRefresh.setOnRefreshListener {

            filters.clear()
            queueName.text.clear()
            queueSeason.text.clear()
            queueEpisode.text.clear()

            recycler.refresh()

            binding.episodeListRefresh.isRefreshing = false
        }
    }

    private fun toggleToolBarState() {

        val appToolbar: AppBarLayout = requireView().findViewById(R.id.app_toolbar)
        val searchToolbar: AppBarLayout = requireView().findViewById(R.id.episode_search_toolbar)

        if (searchMenuIsVisible) {
            searchToolbar.visibility = View.VISIBLE
            appToolbar.visibility = View.GONE
        } else {
            appToolbar.visibility = View.VISIBLE
            searchToolbar.visibility = View.GONE
        }
        searchMenuIsVisible = !searchMenuIsVisible
    }

    private fun initViewModel() {

        val viewModel = ViewModelProvider(this)[EpisodeListViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            viewModel.getEpisodeList(repository, filters)
                .collectLatest {
                    recycler.submitData(it)
                }
        }
    }

    private fun initRecyclerView() {

        binding.episodeListRecycler.apply {
            layoutManager =
                GridLayoutManager(this@EpisodeListFragment.requireContext(), 2)
            recycler = EpisodeListRecyclerViewAdapter()
            adapter =
                recycler.withLoadStateHeaderAndFooter(header = LoadStateAdapterForRecycler { recycler.retry() },
                    footer = LoadStateAdapterForRecycler { recycler.retry() })
        }

        recycler.addLoadStateListener {

            binding.episodeListRecycler.isVisible = it.source.refresh is LoadState.NotLoading
            binding.episodeListProgressbar.isVisible = it.source.refresh is LoadState.Loading
            binding.episodeListRetryButton.isVisible = it.source.refresh is LoadState.Error
            binding.episodeListErrorImage.isVisible = it.source.refresh is LoadState.Error

            if (it.source.refresh is LoadState.Error) {
                Toast.makeText(
                    requireContext(),
                    "Error! Try to refresh internet connection or change filter's settings",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
        binding.episodeListRetryButton.setOnClickListener {
            recycler.retry()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val FRAGMENT_CONTACT_TAG = "EPISODE_LIST_FRAGMENT"

        @JvmStatic
        fun newInstance() =
            EpisodeListFragment()
    }
}