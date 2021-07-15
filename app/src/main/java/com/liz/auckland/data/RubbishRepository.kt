package com.liz.auckland.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.liz.auckland.api.RubbishApi
import com.liz.auckland.app.MainApplication
import com.liz.auckland.util.KEY
import com.liz.auckland.model.Rubbish
import com.liz.auckland.resource.Resource
import kotlinx.coroutines.*
import retrofit2.await
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

class RubbishRepository {

    // Lay danh sach town city
    val getTownCitiesResult = MutableLiveData<Resource<List<String?>?>>()
    // Town city hien tai
    val currentTownCity = MutableLiveData<String?>(null)

    // Lay danh sach suburb locality
    val getSuburbLocalitiesResult = MutableLiveData<Resource<List<String?>?>>()
    // Suburb locality hien tai
    val currentSuburbLocality = MutableLiveData<String?>(null)

    // Lay danh sach road name
    val getRoadNamesResult = MutableLiveData<Resource<List<String?>?>>()
    // Road name hien tai
    val currentRoadName = MutableLiveData<String?>(null)

    // Lay danh sach so nha
    val getAddressNumbersResult = MutableLiveData<Resource<List<String?>?>>()
    // So nha hien tai
    val currentAddressNumber = MutableLiveData<String?>(null)

    // Lay thong tin rubbish
    val getRubbishInfoResult = MutableLiveData<Resource<Rubbish?>>()

    // Dia chi lay rubbish
    val rubbishAn = Transformations.switchMap(currentSuburbLocality) { locality ->
        Transformations.switchMap(currentRoadName) { road ->
            Transformations.map(currentAddressNumber) { address ->
                "$address $road, $locality"
            }
        }
    }

    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)

    // Danh sach du lieu da luu
    var savedData = HashMap<String, Any?>()

    init {
        var s = MainApplication.instant.savedData.clone() as HashMap<String, Any?>?
        if (s.isNullOrEmpty()) {
            s = HashMap()
        }
        savedData = s
    }

    // Lay danh sach town city
    fun getTownCities() {
        getTownCitiesResult.postValue(Resource.Loading("Getting town city list", getTownCitiesResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getTownCities()?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach town city
                    getTownCitiesResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() && !s.contentEquals("town_city") }))
                    // Neu co town city truoc do thi chon
                    if (savedData.containsKey(KEY.CURRENT_TOWN_CITY)) {
                        currentTownCity.postValue(savedData[KEY.CURRENT_TOWN_CITY].toString())
                        savedData.remove(KEY.CURRENT_TOWN_CITY)
                    }
                } else {
                    getTownCitiesResult.postValue(Resource.Error("No town city founds!", getTownCitiesResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getTownCitiesResult.postValue(Resource.Error("Getting town cities error: ${e.message}", getTownCitiesResult.value?.data))
            }
        }
    }

    // Lay danh sach suburb locality
    fun getSuburbLocalities() {
        getSuburbLocalitiesResult.postValue(Resource.Loading("Getting suburb locality list", getSuburbLocalitiesResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getSuburbLocalities(currentTownCity.value)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach suburb locality
                    getSuburbLocalitiesResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
                    // Neu co suburb locality truoc do thi chon
                    if (savedData.containsKey(KEY.CURRENT_SUBURB_LOCALITY)) {
                        currentSuburbLocality.postValue(savedData[KEY.CURRENT_SUBURB_LOCALITY].toString())
                        savedData.remove(KEY.CURRENT_SUBURB_LOCALITY)
                    }
                } else {
                    getSuburbLocalitiesResult.postValue(Resource.Error("No suburb locality founds!", getSuburbLocalitiesResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getSuburbLocalitiesResult.postValue(Resource.Error("Getting suburb localities error: ${e.message}", getSuburbLocalitiesResult.value?.data))
            }
        }
    }

    // Lay danh sach road name
    fun getRoadNames() {
        getRoadNamesResult.postValue(Resource.Loading("Getting road name list", getRoadNamesResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getRoadNames(currentSuburbLocality.value)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach road name
                    getRoadNamesResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
                    // Neu co road name truoc do thi chon
                    if (savedData.containsKey(KEY.CURRENT_ROAD_NAME)) {
                        currentRoadName.postValue(savedData[KEY.CURRENT_ROAD_NAME].toString())
                        savedData.remove(KEY.CURRENT_ROAD_NAME)
                    }
                } else {
                    getRoadNamesResult.postValue(Resource.Error("No road name founds!", getRoadNamesResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getRoadNamesResult.postValue(Resource.Error("Getting road names error: ${e.message}", getRoadNamesResult.value?.data))
            }
        }
    }

    // Lay danh sach so nha
    fun getAddressNumbers() {
        getAddressNumbersResult.postValue(Resource.Loading("Getting address number list", getAddressNumbersResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getAddressNumbers(currentSuburbLocality.value, currentRoadName.value)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach road name
                    getAddressNumbersResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
                    // Neu co address number truoc do thi chon
                    if (savedData.containsKey(KEY.CURRENT_ADDRESS_NUMBER)) {
                        currentAddressNumber.postValue(savedData[KEY.CURRENT_ADDRESS_NUMBER].toString())
                        savedData.remove(KEY.CURRENT_ADDRESS_NUMBER)
                    }
                } else {
                    getAddressNumbersResult.postValue(Resource.Error("No address number founds!", getAddressNumbersResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getAddressNumbersResult.postValue(Resource.Error("Getting address number error: ${e.message}", getAddressNumbersResult.value?.data))
            }
        }
    }

    // Lay thong tin rubbish
    fun getRubbish(an: String?) {
        getRubbishInfoResult.postValue(Resource.Loading("Getting rubbish info", getRubbishInfoResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getRubbish(an)?.await()
                if (result != null) {
                    // Luu lai rubbish info
                    getRubbishInfoResult.postValue(Resource.Success(result))
                } else {
                    getRubbishInfoResult.postValue(Resource.Error("No rubbish info founds!", getRubbishInfoResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getRubbishInfoResult.postValue(Resource.Error("Getting rubbish info error: ${e.message}", getRubbishInfoResult.value?.data))
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}