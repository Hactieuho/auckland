package com.eli.auckland.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.eli.auckland.R
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.databinding.ActivityMainBinding
import com.eli.auckland.model.ApiResult
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
            if (it == ApiResult.ERROR) {
                ToastUtils.showShort(it.value)
                RubbishRepository.instant.getAddressListResult.postValue(null)
            }
        }
        RubbishRepository.instant.currentAddress.observe(this) {
            if (it != null) {
                viewModel.getRubbishInfo()
            }
        }
        RubbishRepository.instant.getRubbishResult.observe(this) {
            if (it != null) {
                ToastUtils.showLong(it.value)
                RubbishRepository.instant.getRubbishResult.postValue(null)
            }
        }
    }

    private fun initUI() {
        // Lay danh sach dia chi
        viewModel.getLocations()
        binding.tvAddressList.setOnClickListener { showLocations() }
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