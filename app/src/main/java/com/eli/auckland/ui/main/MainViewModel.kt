package com.eli.auckland.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
    // Address number hien tai
    val currentAddressNumber = RubbishRepository.instant.currentAddressNumber
    // Thong tin rubbish
    val getRubbishInfoResult = RubbishRepository.instant.getRubbishInfoResult

    // An hien road name
    val roadNameVisibility = Transformations.switchMap(RubbishRepository.instant.getTownCitiesResult) { cities ->
        Transformations.switchMap(currentTownCity) { city ->
            Transformations.map(RubbishRepository.instant.getRoadNamesResult) { roads ->
                if (cities is Resource.Success && !city.isNullOrEmpty() && roads is Resource.Success) {
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
            Transformations.map(RubbishRepository.instant.getAddressNumbersResult) { addresses ->
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
    val rubbishAn = Transformations.switchMap(RubbishRepository.instant.currentTownCity) { city ->
        Transformations.switchMap(currentRoadName) { road ->
            Transformations.map(currentAddressNumber) { address ->
                "$address $road, $city"
            }
        }
    }
    // Dia chi lay rubbish
    val rubbishVisibility = Transformations.switchMap(RubbishRepository.instant.currentTownCity) { city ->
        Transformations.switchMap(currentRoadName) { road ->
            Transformations.map(currentAddressNumber) { address ->
                if (city.isNullOrEmpty() || road.isNullOrEmpty() || address.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    // An hien loading
    val loadingVisibility = Transformations.switchMap(RubbishRepository.instant.getTownCitiesResult) { cities ->
        Transformations.switchMap(RubbishRepository.instant.getRoadNamesResult) { roads ->
            Transformations.switchMap(RubbishRepository.instant.getAddressNumbersResult) { addresses ->
                Transformations.map(RubbishRepository.instant.getRubbishInfoResult) { rubish ->
                    if (cities.isLoading() || roads.isLoading() || addresses.isLoading() || rubish.isLoading()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }
    }

    // Lay danh sach town city
    fun getTownCities() {
        RubbishRepository.instant.getTownCities()
    }

    // Lay danh sach road name
    fun getRoadNames() {
        RubbishRepository.instant.getRoadNames(currentTownCity.value)
    }

    // Lay danh sach address number
    fun getAddressNumbers() {
        RubbishRepository.instant.getAddressNumbers(currentTownCity.value, currentRoadName.value)
    }

    // Lay thong tin rubbish
    fun getRubbishInfo() {
        RubbishRepository.instant.getRubbish(rubbishAn.value)
    }
}