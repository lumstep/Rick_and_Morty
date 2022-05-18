package com.lumstep.rickandmorty.person.person_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.lumstep.rickandmorty.databinding.FragmentPersonListBinding
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class PersonListFragment : Fragment() {

    private var _binding: FragmentPersonListBinding? = null
    private val binding get() = _binding!!

    private var searchMenuIsVisible: Boolean = false

    private var filters = HashMap<String, String>()

    @Inject
    lateinit var recycler: PersonListRecyclerViewAdapter

    @Inject
    lateinit var repository: ListRepository<Person>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPersonListBinding.inflate(inflater, container, false)
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
            requireView().findViewById(R.id.person_list_search_back_button)

        searchButton.setOnClickListener {
            toggleToolBarState()
        }

        returnButton.setOnClickListener {
            toggleToolBarState()
        }

        initFilterMenu()
    }

    private fun initFilterMenu() {

        val queueName: EditText = requireView().findViewById(R.id.person_list_filter_edit_text_name)
        val queueSpecies: EditText =
            requireView().findViewById(R.id.person_list_filter_edit_text_species)
        val queueType: EditText = requireView().findViewById(R.id.person_list_filter_edit_text_type)
        val spinnerStatus: Spinner =
            requireView().findViewById(R.id.person_list_filter_spinner_status)
        val spinnerGender: Spinner =
            requireView().findViewById(R.id.person_list_filter_spinner_gender)

        val searchButton: AppCompatButton =
            requireView().findViewById(R.id.person_list_apply_filters_button)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.person_filters_status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.person_filters_gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }

        searchButton.setOnClickListener {
            filters.clear()

            if (queueName.text.isNotEmpty()) filters["name"] = queueName.text.toString()
            if (queueSpecies.text.isNotEmpty()) filters["species"] = queueSpecies.text.toString()
            if (queueType.text.isNotEmpty()) filters["type"] = queueType.text.toString()
            if (spinnerStatus.selectedItem.toString() != "Any") filters["status"] =
                spinnerStatus.selectedItem.toString()
            if (spinnerGender.selectedItem.toString() != "Any") filters["gender"] =
                spinnerGender.selectedItem.toString()

            val im =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(requireView().windowToken, 0)

            recycler.refresh()

            toggleToolBarState()
        }

        binding.personListRefresh.setOnRefreshListener {

            filters.clear()
            queueName.text.clear()
            queueSpecies.text.clear()
            queueType.text.clear()
            spinnerStatus.setSelection(0)
            spinnerGender.setSelection(0)

            recycler.refresh()

            binding.personListRefresh.isRefreshing = false
        }
    }

    private fun toggleToolBarState() {

        val appToolbar: AppBarLayout = requireView().findViewById(R.id.app_toolbar)
        val searchToolbar: AppBarLayout = requireView().findViewById(R.id.person_search_toolbar)

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

        val viewModel = ViewModelProvider(this)[PersonListViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            viewModel.getPersonList(repository, filters)
                .collectLatest {
                    recycler.submitData(it)
                }
        }
    }


    private fun initRecyclerView() {

        binding.personListRecycler.apply {
            layoutManager =
                GridLayoutManager(this@PersonListFragment.requireContext(), 2)
            adapter =
                recycler.withLoadStateHeaderAndFooter(header = LoadStateAdapterForRecycler { recycler.retry() },
                    footer = LoadStateAdapterForRecycler { recycler.retry() })
        }

        recycler.addLoadStateListener {

            binding.personListRecycler.isVisible = it.source.refresh is LoadState.NotLoading
            binding.personListProgressbar.isVisible = it.source.refresh is LoadState.Loading
            binding.personListRetryButton.isVisible = it.source.refresh is LoadState.Error
            binding.personListErrorImage.isVisible = it.source.refresh is LoadState.Error

            if (it.source.refresh is LoadState.Error) {
                Toast.makeText(
                    requireContext(),
                    "Error! Try to refresh internet connection or change filter's settings",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        binding.personListRetryButton.setOnClickListener {
            recycler.retry()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FRAGMENT_CONTACT_TAG = "PERSON_LIST_FRAGMENT"

        fun newInstance() = PersonListFragment()
    }
}