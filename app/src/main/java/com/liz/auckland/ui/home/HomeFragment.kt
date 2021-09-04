package com.liz.auckland.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.liz.auckland.R
import com.liz.auckland.data.RubbishRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liz.auckland.databinding.FragmentHomeBinding
import com.liz.auckland.ui.main.MainViewModel
import com.liz.auckland.util.showList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    val viewModel: MainViewModel by activityViewModels()
    @Inject
    lateinit var rubbishRepository: RubbishRepository
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        initUI()
        observeViewModel()
        return binding.root
    }

    private fun initUI() {
        // Lay danh sach town city
        binding.tvTownCityList.setOnClickListener { showTownCities() }
        binding.tvSuburbLocalities.setOnClickListener { showSuburbLocalities() }
        binding.tvRoadName.setOnClickListener { showRoadNames() }
        binding.tvAddressNumber.setOnClickListener { showAddressNumbers() }
    }

    private fun observeViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun showTownCities() {
        showList(R.string.choose_an_town_city, rubbishRepository.getTownCitiesResult, viewModel.currentTownCity)
    }

    private fun showSuburbLocalities() {
        showList(R.string.choose_a_suburb_locality, rubbishRepository.getSuburbLocalitiesResult, viewModel.currentSuburbLocality)
    }

    private fun showRoadNames() {
        showList(R.string.choose_a_road_name, rubbishRepository.getRoadNamesResult, viewModel.currentRoadName)
    }

    private fun showAddressNumbers() {
        showList(R.string.choose_an_address_number, rubbishRepository.getAddressNumbersResult, viewModel.currentAddressNumber)
    }

    companion object {
        // Singleton
        val instance = HomeFragment()
    }
}