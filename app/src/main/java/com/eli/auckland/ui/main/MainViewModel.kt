package com.eli.auckland.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.model.Address
import com.eli.auckland.model.Rubbish
import com.eli.auckland.resource.Resource

class MainViewModel : ViewModel() {
    // Dia chi hien tai
    val currentAddress = RubbishRepository.instant.currentAddress
    // Danh sach dia chi
    val addressList = RubbishRepository.instant.addressList
    // Thong tin dia chi hien tai
    val rubbish = RubbishRepository.instant.rubbish

    // Lah danh sach dia chi
    fun getLocations() {
        RubbishRepository.instant.getLocations()
    }

    // Lay thong tin dia chi hien tai
    fun getRubbishInfo() {
        RubbishRepository.instant.getRubbish(currentAddress.value?.id, )
    }
}