package com.liz.auckland.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.databinding.ActivityMainBinding
import com.liz.auckland.resource.Resource
import com.liz.auckland.util.KEY
import com.liz.auckland.util.handleError
import com.liz.auckland.util.onChooseTime
import com.liz.auckland.util.showList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        // Lay danh sach town city khi chon
        binding.tvTownCityList.setOnClickListener { showTownCities() }
        binding.tvSuburbLocalities.setOnClickListener { showSuburbLocalities() }
        binding.tvRoadName.setOnClickListener { showRoadNames() }
        binding.tvAddressNumber.setOnClickListener { showAddressNumbers() }
        // Lay danh sach town city
        viewModel.getTownCities()
    }

    private fun observeViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.currentTownCity.observe(this) {
            // Reset suburb locality
            RubbishRepository.instant.getSuburbLocalitiesResult.postValue(Resource.Success(null))
            viewModel.currentSuburbLocality.postValue(null)
            // Lay danh sach suburb locality khi chon 1 town city
            if (!it.isNullOrEmpty()) {
                viewModel.getSuburbLocalities()
                // Luu lai town city da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_TOWN_CITY, it)
            }
        }
        viewModel.currentSuburbLocality.observe(this) {
            // Reset road name
            RubbishRepository.instant.getRoadNamesResult.postValue(Resource.Success(null))
            viewModel.currentRoadName.postValue(null)
            // Lay danh sach road name khi chon 1 town city
            if (!it.isNullOrEmpty()) {
                viewModel.getRoadNames()
                // Luu lai suburb locality da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_SUBURB_LOCALITY, it)
            }
        }
        viewModel.currentRoadName.observe(this) {
            // Reset address number
            RubbishRepository.instant.getAddressNumbersResult.postValue(Resource.Success(null))
            viewModel.currentAddressNumber.postValue(null)
            // Lay danh sach address number khi chon 1 road name
            if (!it.isNullOrEmpty()) {
                viewModel.getAddressNumbers()
                // Luu lai road name da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_ROAD_NAME, it)
            }
        }
        viewModel.currentAddressNumber.observe(this) {
            // Reset address number
            viewModel.getRubbishInfoResult.postValue(Resource.Success(null))
            // Lay thong tin rubbish khi chon 1 address number
            if (!it.isNullOrEmpty()) {
                viewModel.getRubbishInfo()
                // Luu lai address number da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_ADDRESS_NUMBER, it)
            }
        }
        viewModel.rubbishAn.observe(this, {})

        handleError(RubbishRepository.instant.getTownCitiesResult)
        handleError(RubbishRepository.instant.getSuburbLocalitiesResult)
        handleError(RubbishRepository.instant.getRoadNamesResult)
        handleError(RubbishRepository.instant.getAddressNumbersResult)

        viewModel.addAlarm.observe(this) {
            if (!it.isNullOrEmpty()) {
                val date = when (it) {
                    KEY.HOUSEHOLD_RUBBISH_FROM -> viewModel.getRubbishInfoResult.value?.data?.getFirstHNext()?.from
                    KEY.HOUSEHOLD_RECYCLING_FROM -> viewModel.getRubbishInfoResult.value?.data?.getSecondHNext()?.from
                    else -> null
                }
                val title = when (it) {
                    KEY.HOUSEHOLD_RUBBISH_FROM -> "Household rubbish"
                    KEY.HOUSEHOLD_RECYCLING_FROM -> "Household recycling"
                    else -> ""
                }
                onChooseTime(date, title, this)
                viewModel.addAlarm.postValue(null)
            }
        }
    }

    private fun showTownCities() {
        showList(R.string.choose_an_town_city, RubbishRepository.instant.getTownCitiesResult, viewModel.currentTownCity)
    }

    private fun showSuburbLocalities() {
        showList(R.string.choose_a_suburb_locality, RubbishRepository.instant.getSuburbLocalitiesResult, viewModel.currentSuburbLocality)
    }

    private fun showRoadNames() {
        showList(R.string.choose_a_road_name, RubbishRepository.instant.getRoadNamesResult, viewModel.currentRoadName)
    }

    private fun showAddressNumbers() {
        showList(R.string.choose_an_address_number, RubbishRepository.instant.getAddressNumbersResult, viewModel.currentAddressNumber)
    }
}