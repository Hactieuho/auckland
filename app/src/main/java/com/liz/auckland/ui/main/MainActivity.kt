package com.liz.auckland.ui.main

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.databinding.ActivityMainBinding
import com.liz.auckland.model.BaseFragmentAdapter
import com.liz.auckland.receiver.AlarmReceiver
import com.liz.auckland.resource.Resource
import com.liz.auckland.ui.home.HomeFragment
import com.liz.auckland.ui.info.InfoFragment
import com.liz.auckland.ui.reminder.ReminderFragment
import com.liz.auckland.util.KEY
import com.liz.auckland.util.formatDate
import com.liz.auckland.util.formatRequestCode
import com.liz.auckland.util.formatTime
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initUI()
        observeViewModel()
    }

    private lateinit var adapter: BaseFragmentAdapter
    private fun initUI() {
        // Lay danh sach town city
        viewModel.getTownCities()
        adapter = BaseFragmentAdapter(supportFragmentManager, 0)
        adapter.addFragment(HomeFragment.instance, getString(R.string.home))
        adapter.addFragment(ReminderFragment.instance, getString(R.string.reminder))
        adapter.addFragment(InfoFragment.instance, getString(R.string.info))
        binding.viewpager.adapter = adapter
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            binding.viewpager.currentItem = when(it.itemId) {
                R.id.i_reminder -> 1
                R.id.i_info-> 2
                else -> 0
            }
            true
        }
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.selectedItemId = when(position) {
                    1 -> R.id.i_reminder
                    2 -> R.id.i_info
                    else -> R.id.i_home
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
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

    fun showTownCities() {
        val townCityList = RubbishRepository.instant.getTownCitiesResult.value?.data?.toTypedArray()
        val checkedTownCity = viewModel.currentTownCity.value?.let {
            RubbishRepository.instant.getTownCitiesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.choose_an_town_city))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(townCityList, checkedTownCity) { dialog, which ->
                // Danh dau town city hien tai
                viewModel.currentTownCity.postValue(RubbishRepository.instant.getTownCitiesResult.value?.data?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showSuburbLocalities() {
        val suburbLocalityList = RubbishRepository.instant.getSuburbLocalitiesResult.value?.data?.toTypedArray()
        val checkedLocality = viewModel.currentSuburbLocality.value?.let {
            RubbishRepository.instant.getSuburbLocalitiesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
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
        val roadNameList = RubbishRepository.instant.getRoadNamesResult.value?.data?.toTypedArray()
        val checkedRoadName = viewModel.currentRoadName.value?.let {
            RubbishRepository.instant.getRoadNamesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.choose_a_road_name))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(roadNameList, checkedRoadName) { dialog, which ->
                viewModel.currentRoadName.postValue(RubbishRepository.instant.getRoadNamesResult.value?.data?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showAddressNumbers() {
        val addressNumberList = RubbishRepository.instant.getAddressNumbersResult.value?.data?.toTypedArray()
        val checkedAddressNumber = viewModel.currentAddressNumber.value?.let {
            RubbishRepository.instant.getAddressNumbersResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.choose_an_address_number))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(addressNumberList, checkedAddressNumber) { dialog, which ->
                viewModel.currentAddressNumber.postValue(RubbishRepository.instant.getAddressNumbersResult.value?.data?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun testNotification(view: View) {
        val c = Calendar.getInstance()
        c.add(Calendar.SECOND, 5)
        createAlarmNotification(c.time, "Test")
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