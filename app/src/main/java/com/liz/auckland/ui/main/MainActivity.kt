package com.liz.auckland.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.databinding.ActivityMainBinding
import com.liz.auckland.model.MainPagerAdapter
import com.liz.auckland.resource.Resource
import com.liz.auckland.util.KEY
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        // Lay danh sach town city
        viewModel.getTownCities()
        adapter = MainPagerAdapter(this)
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
        RubbishRepository.instant.getTownCitiesResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showShort(it.message)
            }
        }
        RubbishRepository.instant.getSuburbLocalitiesResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showShort(it.message)
            }
        }
        RubbishRepository.instant.getRoadNamesResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showShort(it.message)
            }
        }
        RubbishRepository.instant.getAddressNumbersResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showShort(it.message)
            }
        }
        viewModel.addAlarm.observe(this) {
            if (!it.isNullOrEmpty()) {
                val date = when (it) {
                    KEY.HOUSEHOLD_RUBBISH_FROM -> viewModel.getRubbishInfoResult.value?.data?.getFirstHNext()?.from
                    KEY.HOUSEHOLD_RUBBISH_TO -> viewModel.getRubbishInfoResult.value?.data?.getFirstHNext()?.to
                    KEY.HOUSEHOLD_RECYCLING_FROM -> viewModel.getRubbishInfoResult.value?.data?.getSecondHNext()?.from
                    KEY.HOUSEHOLD_RECYCLING_TO -> viewModel.getRubbishInfoResult.value?.data?.getSecondHNext()?.to
                    KEY.COMMERCIAL_RUBBISH_FROM -> viewModel.getRubbishInfoResult.value?.data?.getFirstCNext()?.from
                    KEY.COMMERCIAL_RUBBISH_TO -> viewModel.getRubbishInfoResult.value?.data?.getFirstCNext()?.to
                    KEY.COMMERCIAL_RECYCLING_FROM -> viewModel.getRubbishInfoResult.value?.data?.getSecondHNext()?.from
                    KEY.COMMERCIAL_RECYCLING_TO -> viewModel.getRubbishInfoResult.value?.data?.getSecondHNext()?.to
                    else -> null
                }
                val title = when (it) {
                    KEY.HOUSEHOLD_RUBBISH_FROM -> "Household rubbish from ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.HOUSEHOLD_RUBBISH_TO -> "Household rubbish to ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.HOUSEHOLD_RECYCLING_FROM -> "Household recycling from ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.HOUSEHOLD_RECYCLING_TO -> "Household recycling to ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.COMMERCIAL_RUBBISH_FROM -> "Commercial rubbish from ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.COMMERCIAL_RUBBISH_TO -> "Commercial rubbish to ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.COMMERCIAL_RECYCLING_FROM -> "Commercial recycling from ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    KEY.COMMERCIAL_RECYCLING_TO -> "Commercial recycling to ${viewModel.getRubbishInfoResult.value?.data?.location}"
                    else -> ""
                }
                createAlarm(date, title)
                viewModel.addAlarm.postValue(null)
            }
        }
    }

    fun createAlarm(date: Date?, title: String?) {
        val i = Intent(AlarmClock.ACTION_SET_ALARM)
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val extraDays = calendar.get(Calendar.DAY_OF_WEEK)
        val extraHours = calendar.get(Calendar.HOUR_OF_DAY)
        val extraMinutes = calendar.get(Calendar.MINUTE)
        i.putExtra(AlarmClock.EXTRA_MESSAGE, title)
        i.putExtra(AlarmClock.EXTRA_DAYS, extraDays)
        i.putExtra(AlarmClock.EXTRA_HOUR, extraHours)
        i.putExtra(AlarmClock.EXTRA_MINUTES, extraMinutes)
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        startActivity(i)
    }
}