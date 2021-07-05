package com.liz.auckland.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.databinding.ActivityMainBinding
import com.liz.auckland.receiver.AlarmReceiver
import com.liz.auckland.resource.Resource
import com.liz.auckland.util.KEY
import com.liz.auckland.util.formatDate
import com.liz.auckland.util.formatRequestCode
import com.liz.auckland.util.formatTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liz.auckland.databinding.FragmentHomeBinding
import com.liz.auckland.ui.main.MainViewModel
import java.util.*

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
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
        val townCityList = RubbishRepository.instant.getTownCitiesResult.value?.data?.toTypedArray()
        val checkedTownCity = viewModel.currentTownCity.value?.let {
            RubbishRepository.instant.getTownCitiesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
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
        val roadNameList = RubbishRepository.instant.getRoadNamesResult.value?.data?.toTypedArray()
        val checkedRoadName = viewModel.currentRoadName.value?.let {
            RubbishRepository.instant.getRoadNamesResult.value?.data?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(requireContext())
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

        MaterialAlertDialogBuilder(requireContext())
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

    fun createAlarmNotification(date: Date?, title: String?) {
        // Get AlarmManager instance
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Intent part
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = KEY.ALARMS
        intent.putExtra(KEY.NOTIFICATION_TITLE, title)
        // Alarm time
        if (date != null) {
            val pendingIntent = PendingIntent.getBroadcast(context, date.formatRequestCode(), intent, 0)
            val alarmTime = date.time
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            ToastUtils.showLong("Reminder me at ${date.formatDate()} ${date.formatTime()}")
        }
    }

    fun cancelAlarmNotification(date: Date?) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        if (date != null) {
            val pendingIntent = PendingIntent.getBroadcast(activity?.applicationContext, date.formatRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
            ToastUtils.showLong("Cancel reminder ${date.formatDate()} ${date.formatTime()}")
        }
    }

    companion object {
        // Singleton
        val instance = HomeFragment()
    }
}