package com.liz.auckland.ui.main

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
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
import com.liz.auckland.model.ScreenSlidePagerAdapter
import com.liz.auckland.receiver.AlarmReceiver
import com.liz.auckland.resource.Resource
import com.liz.auckland.util.KEY
import com.liz.auckland.util.formatDate
import com.liz.auckland.util.formatRequestCode
import com.liz.auckland.util.formatTime
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var adapter: ScreenSlidePagerAdapter

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
        adapter = ScreenSlidePagerAdapter(this)
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
        viewModel.addedAlarm.observe(this) {
            if (it != null) {
                createAlarmNotification(it, viewModel.rubbishAn.value)
                viewModel.addedAlarm.postValue(null)
            }
        }
        viewModel.canceledAlarm.observe(this) {
            if (it != null) {
                cancelAlarmNotification(it)
                viewModel.canceledAlarm.postValue(null)
            }
        }
    }

    @SuppressLint("NewApi")
    fun createAlarmNotification(date: Date?, title: String?) {
        // Get AlarmManager instance
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Intent part
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = KEY.ALARMS
        intent.putExtra(KEY.NOTIFICATION_TITLE, title)
        // Alarm time
        if (date != null) {
            val pendingIntent = PendingIntent.getBroadcast(this, date.formatRequestCode(), intent, 0)
            val alarmTime = date.time
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            ToastUtils.showLong("Reminder me at ${date.formatDate()} ${date.formatTime()}")
        }
    }

    fun cancelAlarmNotification(date: Date?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        if (date != null) {
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, date.formatRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
            ToastUtils.showLong("Cancel reminder ${date.formatDate()} ${date.formatTime()}")
        }
    }
}