package com.liz.auckland.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.liz.auckland.R
import com.liz.auckland.data.RubbishRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liz.auckland.databinding.FragmentHomeBinding
import com.liz.auckland.ui.main.MainViewModel
import java.util.jar.Attributes

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    val viewModel: MainViewModel by activityViewModels()

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

    fun showTownCities() {
        val townCityList = RubbishRepository.instant.getTownCitiesResult.value?.data?.sortedBy { it }?.toTypedArray()
        val checkedTownCity = viewModel.currentTownCity.value?.let {
            townCityList?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_an_town_city))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(townCityList, checkedTownCity) { dialog, which ->
                // Danh dau town city hien tai
                viewModel.currentTownCity.postValue(townCityList?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showSuburbLocalities() {
        val suburbLocalityList = RubbishRepository.instant.getSuburbLocalitiesResult.value?.data?.sortedBy { it }?.toTypedArray()
        val checkedLocality = viewModel.currentSuburbLocality.value?.let {
            suburbLocalityList?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_a_suburb_locality))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(suburbLocalityList, checkedLocality) { dialog, which ->
                // Danh dau town city hien tai
                viewModel.currentSuburbLocality.postValue(suburbLocalityList?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showRoadNames() {
        val roadNameList = RubbishRepository.instant.getRoadNamesResult.value?.data?.sortedBy { it }?.toTypedArray()
        val checkedRoadName = viewModel.currentRoadName.value?.let {
            roadNameList?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_a_road_name))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(roadNameList, checkedRoadName) { dialog, which ->
                viewModel.currentRoadName.postValue(roadNameList?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showAddressNumbers() {
        val addressNumberList = RubbishRepository.instant.getAddressNumbersResult.value?.data?.sortedBy { it }?.toTypedArray()
        val checkedAddressNumber = viewModel.currentAddressNumber.value?.let {
            addressNumberList?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_an_address_number))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(addressNumberList, checkedAddressNumber) { dialog, which ->
                viewModel.currentAddressNumber.postValue(addressNumberList?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        // Singleton
        val instance = HomeFragment()
        var key = "a"
        val name = "2errt"
        const val PUBLIC_NAME = "2errt"
    }
}