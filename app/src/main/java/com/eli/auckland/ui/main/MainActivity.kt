package com.eli.auckland.ui.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.eli.auckland.R
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.databinding.ActivityMainBinding
import com.eli.auckland.receiver.AlarmReceiver
import com.eli.auckland.resource.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

    private fun initUI() {
        // Lay danh sach town city
        viewModel.getTownCities()
        binding.tvTownCityList.setOnClickListener { showTownCities() }
        binding.tvRoadName.setOnClickListener { showRoadNames() }
        binding.tvAddressNumber.setOnClickListener { showAddressNumbers() }
    }

    private fun observeViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.currentTownCity.observe(this) {
            // Reset road name
            RubbishRepository.instant.getRoadNamesResult.postValue(Resource.Success(null))
            viewModel.currentRoadName.postValue(null)
            // Lay danh sach road name khi chon 1 town city
            if (!it.isNullOrEmpty()) {
                viewModel.getRoadNames()
            }
        }
        viewModel.currentRoadName.observe(this) {
            // Reset address number
            RubbishRepository.instant.getAddressNumbersResult.postValue(Resource.Success(null))
            viewModel.currentAddressNumber.postValue(null)
            // Lay danh sach address number khi chon 1 road name
            if (!it.isNullOrEmpty()) {
                viewModel.getAddressNumbers()
            }
        }
        viewModel.currentAddressNumber.observe(this) {
            // Reset address number
            viewModel.getRubbishInfoResult.postValue(Resource.Success(null))
            // Lay thong tin rubbish khi chon 1 address number
            if (!it.isNullOrEmpty()) {
                viewModel.getRubbishInfo()
            }
        }
        viewModel.rubbishAn.observe(this, {})
        RubbishRepository.instant.getTownCitiesResult.observe(this) {
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

    fun showRoadNames() {
        val roadNameList = RubbishRepository.instant.getRoadNamesResult.value?.data?.toTypedArray()
        val checkedRoadName = viewModel.currentRoadName.value?.let {
            RubbishRepository.instant.getRoadNamesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.choose_road_name))
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
            .setTitle(resources.getString(R.string.choose_address_number))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(addressNumberList, checkedAddressNumber) { dialog, which ->
                viewModel.currentAddressNumber.postValue(RubbishRepository.instant.getAddressNumbersResult.value?.data?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun alarm() {
        // Get AlarmManager instance
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Intent part
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = "FOO_ACTION"
        intent.putExtra("KEY_FOO_STRING", "Medium AlarmManager Demo")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        // Alarm time
        val ALARM_DELAY_IN_SECOND = 1
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L
        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
    }
}