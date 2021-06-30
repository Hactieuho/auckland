package com.eli.auckland.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.model.Address
import com.eli.auckland.model.Rubbish
import com.eli.auckland.resource.Resource

class MainViewModel : ViewModel() {
    // Town city hien tai
    val currentTownCity = RubbishRepository.instant.currentTownCity
    // Road name hien tai
    val currentRoadName = RubbishRepository.instant.currentRoadName

    // Lay danh sach town city
    fun getTownCities() {
        RubbishRepository.instant.getTownCities()
    }

    // Lay danh sach road name
    fun getRoadNames(locality: String?) {
        RubbishRepository.instant.getRoadNames(locality)
    }
}