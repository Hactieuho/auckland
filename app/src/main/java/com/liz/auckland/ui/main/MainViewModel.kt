package com.liz.auckland.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.di.*
import com.liz.auckland.ui.main.di.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val rubbishRepository: RubbishRepository,
    // Town city hien tai
    @CurrentTownCity val currentTownCity: MutableLiveData<String?>,
    // Suburb locality hien tai
    @CurrentSuburbLocality val currentSuburbLocality: MutableLiveData<String?>,
    // Road name hien tai
    @CurrentRoadName val currentRoadName: MutableLiveData<String?>,
    // Address number hien tai
    @CurrentAddressNumber val currentAddressNumber: MutableLiveData<String?>,
    // Dia chi lay rubbish
    @RubbishAn val rubbishAn: LiveData<String>?,
    // An hien suburb locality
    @SuburbLocalityVisibility val suburbLocalityVisibility: LiveData<Int>,
    // An hien road name
    @RoadNameVisibility val roadNameVisibility: LiveData<Int>,
    // An hien address number
    @AddressNumberVisibility val addressNumberVisibility: LiveData<Int>,
    // An hien rubbish info
    @RubbishInfoVisibility val rubbishInfoVisibility: LiveData<Int>,
    // An hien dia chi lay rubbish
    @RubbishVisibility val rubbishVisibility: LiveData<Int>,
    // An hien loading
    @LoadingVisibility val loadingVisibility: LiveData<Int>,
    // An hien alarms
    @AlarmEnabled val alarmEnabled: LiveData<Boolean>
) : ViewModel() {
    // Thong tin rubbish
    val getRubbishInfoResult = rubbishRepository.getRubbishInfoResult
    // Them alarm
    val addAlarm = MutableLiveData<String?>(null)

    // Lay danh sach town city
    fun getTownCities() {
        rubbishRepository.getTownCities()
    }

    // Lay danh sach suburb locality
    fun getSuburbLocalities() {
        rubbishRepository.getSuburbLocalities()
    }

    // Lay danh sach road name
    fun getRoadNames() {
        rubbishRepository.getRoadNames()
    }

    // Lay danh sach address number
    fun getAddressNumbers() {
        rubbishRepository.getAddressNumbers()
    }

    // Lay thong tin rubbish
    fun getRubbishInfo() {
        rubbishRepository.getRubbish()
    }

    val alarm: (String?)->Unit = { addAlarm.postValue(it) }

}