package com.liz.auckland.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val rubbishRepository: RubbishRepository
) : ViewModel() {
    // Town city hien tai
    val currentTownCity = rubbishRepository.currentTownCity
    // Suburb locality hien tai
    val currentSuburbLocality = rubbishRepository.currentSuburbLocality
    // Road name hien tai
    val currentRoadName = rubbishRepository.currentRoadName
    // Address number hien tai
    val currentAddressNumber = rubbishRepository.currentAddressNumber
    // Thong tin rubbish
    val getRubbishInfoResult = rubbishRepository.getRubbishInfoResult
    // Them alarm
    val addAlarm = MutableLiveData<String?>(null)

    // An hien suburb locality
    val suburbLocalityVisibility = Transformations.switchMap(rubbishRepository.getTownCitiesResult) { cities ->
        Transformations.switchMap(currentTownCity) { city ->
            Transformations.map(rubbishRepository.getSuburbLocalitiesResult) { localities ->
                if (cities is Resource.Success && !city.isNullOrEmpty() && localities is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
    // An hien road name
    val roadNameVisibility = Transformations.switchMap(suburbLocalityVisibility) { localityVisible ->
        Transformations.switchMap(currentSuburbLocality) { locality ->
            Transformations.map(rubbishRepository.getRoadNamesResult) { roads ->
                if (localityVisible == View.VISIBLE && !locality.isNullOrEmpty() && roads is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
    // An hien address number
    val addressNumberVisibility = Transformations.switchMap(roadNameVisibility) { roadVisible ->
        Transformations.switchMap(currentRoadName) { road ->
            Transformations.map(rubbishRepository.getAddressNumbersResult) { addresses ->
                if (roadVisible == View.VISIBLE && !road.isNullOrEmpty() && addresses is Resource.Success) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
    // An hien rubbish info
    val rubbishInfoVisibility = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(getRubbishInfoResult) { rubbish ->
            Transformations.map(currentAddressNumber) { address ->
                if (addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    // Dia chi lay rubbish
    val rubbishAn = rubbishRepository.rubbishAn
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
        rubbishRepository.getRubbish(rubbishAn.value)
    }

    // An hien alarms
    val alarmEnabled = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(getRubbishInfoResult) { rubbish ->
            Transformations.map(currentAddressNumber) { address ->
                addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()
            }
        }
    }

    val alarm: (String?)->Unit = { addAlarm.postValue(it) }

}