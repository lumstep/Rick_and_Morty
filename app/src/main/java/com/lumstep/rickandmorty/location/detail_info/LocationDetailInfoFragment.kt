package com.lumstep.rickandmorty.location.detail_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lumstep.rickandmorty.FragmentNavigator
import com.lumstep.rickandmorty.appComponent
import com.lumstep.rickandmorty.databinding.FragmentLocationDetailInfoBinding
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.person.Person
import javax.inject.Inject

class LocationDetailInfoFragment : Fragment() {
    private var _binding: FragmentLocationDetailInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navigator: FragmentNavigator

    @Inject
    lateinit var repository: LocationDetailRepository
    private lateinit var viewModel: LocationDetailInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailInfoBinding.inflate(inflater, container, false)
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
        binding.locationDetailInfoRefresh.setOnRefreshListener {
            viewModel.updateLocation(requireArguments().getInt(LOCATION_ID))
            binding.locationDetailInfoRefresh.isRefreshing = false
        }
    }

    private fun initFloatActionButton() {
        binding.locationDetailInfoFab.setOnClickListener {
            navigator.closeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[LocationDetailInfoViewModel::class.java]

        viewModel.initRepository(repository)
        viewModel.updateLocation(requireArguments().getInt(LOCATION_ID))

        viewModel.locationDetailInfo.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                showLocationInfo(it)
                viewModel.updatePersonList(it.residents)
            } else {
                binding.locationListErrorImage.isVisible = true
                binding.locationListRetryButton.isVisible = true
                binding.locationListRetryButton.setOnClickListener {
                    viewModel.updateLocation(requireArguments().getInt(LOCATION_ID))
                }
            }
        }

        viewModel.locationPersonList.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                initRecyclerView(it)
                binding.locationDetailInfoProgressBar.isVisible = false
            } else {
                binding.locationListErrorImage.isVisible = true
                binding.locationListRetryButton.isVisible = true
                binding.locationListRetryButton.setOnClickListener {
                    viewModel.updatePersonList(viewModel.locationDetailInfo.value?.residents)
                }
            }
        }
    }

    private fun initRecyclerView(persons: List<Person>) {
        binding.locationDetailInfoRecycler.apply {
            layoutManager =
                GridLayoutManager(this@LocationDetailInfoFragment.requireContext(), 2)
            adapter = LocationPersonRecyclerViewAdapter(persons)
        }
    }

    private fun showLocationInfo(location: Location) {
        Log.d("MAIN", "${location.name}")

        binding.locationDetailInfoGrid.visibility = View.VISIBLE
        binding.locationDetailInfoName.text = location.name
        binding.locationDetailInfoType.text = location.type
        binding.locationDetailInfoDimension.text = location.dimension
        binding.locationDetailInfoUrl.text = location.url
        binding.locationDetailInfoCreated.text = location.created
    }

    companion object {
        const val FRAGMENT_CONTACT_TAG = "LOCATION_DETAIL_INFO_FRAGMENT"
        private const val LOCATION_ID = "LOCATION_ID"
        fun newInstance(id: Int) = LocationDetailInfoFragment().also {
            it.arguments = Bundle().apply {
                putInt(LOCATION_ID, id)
            }
        }
    }

}