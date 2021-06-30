package com.eli.auckland.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.eli.auckland.R
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.databinding.ActivityMainBinding
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

    private fun observeViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        RubbishRepository.instant.getAddressListResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showShort(it.message)
                RubbishRepository.instant.getAddressListResult.postValue(null)
            }
        }
        RubbishRepository.instant.currentAddress.observe(this) {
            if (it != null) {
                viewModel.getRubbishInfo()
            }
        }
        RubbishRepository.instant.getRubbishResult.observe(this) {
            if (it is Resource.Error) {
                ToastUtils.showLong(it.message)
                RubbishRepository.instant.getRubbishResult.postValue(null)
            }
        }
    }

    private fun initUI() {
        // Lay danh sach town city
        viewModel.getTownCities()
        binding.tvTownCityList.setOnClickListener { showTownCities() }
        binding.tvAddressList.setOnClickListener { showLocations() }
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
                RubbishRepository.instant.currentTownCity.postValue(RubbishRepository.instant.getTownCitiesResult.value?.data?.get(which))
                dialog.dismiss()
            }
            .show()
    }

    fun showLocations() {
        val addressList = viewModel.addressList.value?.map { address -> address.name }?.toTypedArray()
        val checkedAddress = viewModel.currentAddress.value?.let {
            viewModel.addressList.value?.indexOf(
                it
            )
        } ?: 0

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.choose_an_address))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
            .setSingleChoiceItems(addressList, checkedAddress) { dialog, which ->
                viewModel.currentAddress.postValue(viewModel.addressList.value?.get(which))
                dialog.dismiss()
            }
            .show()
    }
}