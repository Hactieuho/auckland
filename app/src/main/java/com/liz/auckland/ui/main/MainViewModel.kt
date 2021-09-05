package com.liz.auckland.ui.main

import android.view.View
import androidx.lifecycle.*
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.di.*
import com.liz.auckland.di.main.*
import com.liz.auckland.resource.Resource
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
    // An hien alarms
    @AlarmEnabled val alarmEnabled: LiveData<Boolean>
) : ViewModel() {
    // Thong tin rubbish
    val getRubbishInfoResult = rubbishRepository.getRubbishInfoResult
    // Them alarm
    val addAlarm = MutableLiveData<String?>(null)

    // An hien dia chi lay rubbish
    val rubbishVisibility = Transformations.switchMap(currentTownCity) { city ->
        Transformations.switchMap(currentSuburbLocality) { locality ->
            Transformations.switchMap(currentRoadName) { road ->
                Transformations.map(currentAddressNumber) { address ->
                    if (city.isNullOrEmpty() || locality.isNullOrEmpty() || road.isNullOrEmpty() || address.isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
    }

    // An hien loading
    val loadingVisibility = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
        Transformations.switchMap(rubbishRepository.getSuburbLocalitiesResult) { localities ->
            Transformations.switchMap(rubbishRepository.getRoadNamesResult) { roads ->
                Transformations.switchMap(rubbishRepository.getAddressNumbersResult) { addresses ->
                    Transformations.map(rubbishRepository.getRubbishInfoResult) { rubish ->
                        if (cities.isLoading() || localities.isLoading() || roads.isLoading() || addresses.isLoading() || rubish.isLoading()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
            }
        }
    }

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