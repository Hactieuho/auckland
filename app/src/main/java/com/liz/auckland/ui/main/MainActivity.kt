package com.liz.auckland.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.databinding.ActivityMainBinding
import com.liz.auckland.ui.adapter.MainPagerAdapter
import com.liz.auckland.resource.Resource
import com.liz.auckland.util.KEY
import com.liz.auckland.util.handleError
import com.liz.auckland.util.onChooseTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()
    @Inject
    lateinit var adapter: MainPagerAdapter
    @Inject
    lateinit var rubbishRepository: RubbishRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        // Lay danh sach town city
        rubbishRepository.getTownCities()
        binding.viewpager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = when(position) {
                1 -> getString(R.string.reminder)
                2 -> getString(R.string.info)
                else -> getString(R.string.home)
            }
            val icon = when(position) {
                1 -> R.drawable.ic_alarm
                2 -> R.drawable.ic_info
                else -> R.drawable.ic_home
            }
            tab.icon = ContextCompat.getDrawable(this, icon)
        }.attach()
    }

    private fun observeViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        rubbishRepository.currentTownCity.observe(this) {
            // Reset suburb locality
            rubbishRepository.getSuburbLocalitiesResult.postValue(Resource.Success(null))
            rubbishRepository.currentSuburbLocality.postValue(null)
            // Lay danh sach suburb locality khi chon 1 town city
            if (!it.isNullOrEmpty()) {
                rubbishRepository.getSuburbLocalities()
                // Luu lai town city da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_TOWN_CITY, it)
            }
        }
        rubbishRepository.currentSuburbLocality.observe(this) {
            // Reset road name
            rubbishRepository.getRoadNamesResult.postValue(Resource.Success(null))
            rubbishRepository.currentRoadName.postValue(null)
            // Lay danh sach road name khi chon 1 town city
            if (!it.isNullOrEmpty()) {
                rubbishRepository.getRoadNames()
                // Luu lai suburb locality da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_SUBURB_LOCALITY, it)
            }
        }
        rubbishRepository.currentRoadName.observe(this) {
            // Reset address number
            rubbishRepository.getAddressNumbersResult.postValue(Resource.Success(null))
            rubbishRepository.currentAddressNumber.postValue(null)
            // Lay danh sach address number khi chon 1 road name
            if (!it.isNullOrEmpty()) {
                rubbishRepository.getAddressNumbers()
                // Luu lai road name da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_ROAD_NAME, it)
            }
        }
        rubbishRepository.currentAddressNumber.observe(this) {
            // Reset address number
            rubbishRepository.getRubbishInfoResult.postValue(Resource.Success(null))
            // Lay thong tin rubbish khi chon 1 address number
            if (!it.isNullOrEmpty()) {
                rubbishRepository.getRubbish()
                // Luu lai address number da chon
                MainApplication.instant.saveToSharePre(KEY.CURRENT_ADDRESS_NUMBER, it)
            }
        }

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
}