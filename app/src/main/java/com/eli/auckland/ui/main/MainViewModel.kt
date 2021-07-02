package com.eli.auckland.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.eli.auckland.data.RubbishRepository
import com.eli.auckland.resource.Resource
import com.eli.auckland.util.formatKey
import java.util.*
import kotlin.collections.HashMap

class MainViewModel : ViewModel() {
    // Town city hien tai
    val currentTownCity = RubbishRepository.instant.currentTownCity
    // Suburb locality hien tai
    val currentSuburbLocality = RubbishRepository.instant.currentSuburbLocality
    // Road name hien tai
    val currentRoadName = RubbishRepository.instant.currentRoadName
    // Address number hien tai
    val currentAddressNumber = RubbishRepository.instant.currentAddressNumber
    // Thong tin rubbish
    val getRubbishInfoResult = RubbishRepository.instant.getRubbishInfoResult
    // Danh sach alarm
    val alarms = MutableLiveData(HashMap<String, Date>())

    // An hien suburb locality
    val suburbLocalityVisibility = Transformations.switchMap(RubbishRepository.instant.getTownCitiesResult) { cities ->
        Transformations.switchMap(currentTownCity) { city ->
            Transformations.map(RubbishRepository.instant.getSuburbLocalitiesResult) { localities ->
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
            Transformations.map(RubbishRepository.instant.getRoadNamesResult) { roads ->
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
    val rubbishAn = RubbishRepository.instant.rubbishAn
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
    val loadingVisibility = Transformations.switchMap(RubbishRepository.instant.getTownCitiesResult) { cities ->
        Transformations.switchMap(RubbishRepository.instant.getSuburbLocalitiesResult) { localities ->
            Transformations.switchMap(RubbishRepository.instant.getRoadNamesResult) { roads ->
                Transformations.switchMap(RubbishRepository.instant.getAddressNumbersResult) { addresses ->
                    Transformations.map(RubbishRepository.instant.getRubbishInfoResult) { rubish ->
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
        RubbishRepository.instant.getTownCities()
    }

    // Lay danh sach suburb locality
    fun getSuburbLocalities() {
        RubbishRepository.instant.getSuburbLocalities()
    }

    // Lay danh sach road name
    fun getRoadNames() {
        RubbishRepository.instant.getRoadNames()
    }

    // Lay danh sach address number
    fun getAddressNumbers() {
        RubbishRepository.instant.getAddressNumbers()
    }

    // Lay thong tin rubbish
    fun getRubbishInfo() {
        RubbishRepository.instant.getRubbish(rubbishAn.value)
    }

    // An hien alarms
    val alarmEnabled = Transformations.switchMap(addressNumberVisibility) { addressVisible ->
        Transformations.switchMap(getRubbishInfoResult) { rubbish ->
            Transformations.map(currentAddressNumber) { address ->
                addressVisible == View.VISIBLE && rubbish is Resource.Success && !address.isNullOrEmpty()
            }
        }
    }
    // Chon/khong chon alarm
    val alarmFromHouseholdChecked = Transformations.switchMap(RubbishRepository.instant.getRubbishInfoResult) { rubbishInfo ->
        Transformations.map(alarms) { a ->
            val key = rubbishInfo.data?.getFirstHNext()?.from?.formatKey()
            rubbishInfo.isSuccess() && a.containsKey(key)
        }
    }
    val alarmToHouseholdChecked = Transformations.switchMap(RubbishRepository.instant.getRubbishInfoResult) { rubbishInfo ->
        Transformations.map(alarms) { a ->
            val key = rubbishInfo.data?.getFirstHNext()?.to?.formatKey()
            rubbishInfo.isSuccess() && a.containsKey(key)
        }
    }
    val alarmFromCommercialChecked = Transformations.switchMap(RubbishRepository.instant.getRubbishInfoResult) { rubbishInfo ->
        Transformations.map(alarms) { a ->
            val key = rubbishInfo.data?.getFirstCNext()?.from?.formatKey()
            rubbishInfo.isSuccess() && a.containsKey(key)
        }
    }
    val alarmToCommericialChecked = Transformations.switchMap(RubbishRepository.instant.getRubbishInfoResult) { rubbishInfo ->
        Transformations.map(alarms) { a ->
            val key = rubbishInfo.data?.getFirstCNext()?.to?.formatKey()
            rubbishInfo.isSuccess() && a.containsKey(key)
        }
    }

    val check: (Date?, Boolean?)->Unit = { d, b -> alarm(d, b) }

    fun alarm(date: Date?, checked: Boolean?) {
        var a = alarms.value
        if (checked != null) {
            if (a == null) {
                a = HashMap()
            }
            val key = date?.formatKey()
            if (a.containsKey(key) && !checked) {
                a.remove(key)
            } else {
                date?.let { d -> a?.put(key.toString(), d) }
            }
        } else {
            if (a == null) {
                a = HashMap()
            }
            val key = date?.formatKey()
            if (a.containsKey(key)) {
                a.remove(key)
            } else {
                date?.let { d -> a.put(key.toString(), d) }
            }
        }
        alarms.postValue(a)
    }
}