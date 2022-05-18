package com.lumstep.rickandmorty.location.location_list

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
import com.lumstep.rickandmorty.databinding.FragmentLocationListBinding
import com.lumstep.rickandmorty.location.Location
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class LocationListFragment : Fragment() {

    private var _binding: FragmentLocationListBinding? = null
    private val binding get() = _binding!!

    private var searchMenuIsVisible: Boolean = false

    private var filters = HashMap<String, String>()

    @Inject
    lateinit var recycler: LocationListRecyclerViewAdapter

    @Inject
    lateinit var repository: ListRepository<Location>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLocationListBinding.inflate(inflater, container, false)
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
            requireView().findViewById(R.id.location_list_search_back_button)

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
            requireView().findViewById(R.id.location_list_filter_edit_text_name)
        val queueType: EditText =
            requireView().findViewById(R.id.location_list_filter_edit_text_type)
        val queueDimension: EditText =
            requireView().findViewById(R.id.location_list_filter_edit_text_dimension)

        val searchButton: AppCompatButton =
            requireView().findViewById(R.id.location_list_apply_filters_button)

        searchButton.setOnClickListener {
            filters.clear()

            if (queueName.text.isNotEmpty()) filters["name"] = queueName.text.toString()
            if (queueType.text.isNotEmpty()) filters["type"] = queueType.text.toString()
            if (queueDimension.text.isNotEmpty()) filters["dimension"] = queueDimension.text.toString()


            val im =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(requireView().windowToken, 0)

            recycler.refresh()

            toggleToolBarState()
        }

        binding.locationListRefresh.setOnRefreshListener {

            filters.clear()
            queueName.text.clear()
            queueType.text.clear()
            queueDimension.text.clear()

            recycler.refresh()

            binding.locationListRefresh.isRefreshing = false
        }
    }

    private fun toggleToolBarState() {

        val appToolbar: AppBarLayout = requireView().findViewById(R.id.app_toolbar)
        val searchToolbar: AppBarLayout = requireView().findViewById(R.id.location_search_toolbar)

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

        val viewModel = ViewModelProvider(this)[LocationListViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            viewModel.getLocationList(repository, filters)
                .collectLatest {
                    recycler.submitData(it)
                }
        }
    }


    private fun initRecyclerView() {

        binding.locationListRecycler.apply {
            layoutManager =
                GridLayoutManager(this@LocationListFragment.requireContext(), 2)
            adapter =
                recycler.withLoadStateHeaderAndFooter(header = LoadStateAdapterForRecycler { recycler.retry() },
                    footer = LoadStateAdapterForRecycler { recycler.retry() })
        }

        recycler.addLoadStateListener {

            binding.locationListRecycler.isVisible = it.source.refresh is LoadState.NotLoading
            binding.locationListProgressbar.isVisible = it.source.refresh is LoadState.Loading
            binding.locationListRetryButton.isVisible = it.source.refresh is LoadState.Error
            binding.locationListErrorImage.isVisible = it.source.refresh is LoadState.Error

            if (it.source.refresh is LoadState.Error) {
                Toast.makeText(
                    requireContext(),
                    "Error! Try to refresh internet connection or change filter's settings",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        binding.locationListRetryButton.setOnClickListener {
            recycler.retry()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FRAGMENT_CONTACT_TAG = "LOCATION_LIST_FRAGMENT"

        fun newInstance() = LocationListFragment()
    }
}