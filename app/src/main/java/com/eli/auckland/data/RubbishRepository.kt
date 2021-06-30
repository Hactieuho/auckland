package com.eli.auckland.data

import androidx.lifecycle.MutableLiveData
import com.eli.auckland.api.RubbishApi
import com.eli.auckland.app.MainApplication
import com.eli.auckland.util.KEY
import com.eli.auckland.model.Address
import com.eli.auckland.resource.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.await
import java.lang.Exception

class RubbishRepository {

    // Lay danh sach town city
    val getTownCitiesResult = MutableLiveData<Resource<List<String?>?>>()
    // Town city hien tai
    val currentTownCity = MutableLiveData<String?>(null)

    // Lay danh sach road name
    val getRoadNamesResult = MutableLiveData<Resource<List<String?>?>>()
    // Road name hien tai
    val currentRoadName = MutableLiveData<String?>(null)

    // Lay danh sach so nha
    val getAddressNumbersResult = MutableLiveData<Resource<List<String?>?>>()
    // So nha hien tai
    val currentAddressNumber = MutableLiveData<String?>(null)


    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        // Lay danh sach dia chi da luu
        val addresses = MainApplication.instant.getListFromSharedPre(KEY.ADDRESS_LIST)
        if (addresses != null) {
        }
        // Lay address da luu
        val address = MainApplication.instant.getFromSharedPre<Address>(KEY.CURRENT_ADDRESS)
        if (address != null) {
        }
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
                } else {
                    getTownCitiesResult.postValue(Resource.Error("No town city founds!", getTownCitiesResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getTownCitiesResult.postValue(Resource.Error("Getting town cities error: ${e.message}", getTownCitiesResult.value?.data))
            }
        }
    }

    // Lay danh sach road name
    fun getRoadNames(locality: String?) {
        getRoadNamesResult.postValue(Resource.Loading("Getting road name list", getRoadNamesResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getRoadNames(locality)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach road name
                    getRoadNamesResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
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
    fun getAddressNumbers(locality: String?, roadName: String?) {
        getAddressNumbersResult.postValue(Resource.Loading("Getting address number list", getAddressNumbersResult.value?.data))
        ioScope.launch {
            try {
                val result = RubbishApi.api.getAddressNumbers(locality, roadName)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach road name
                    getAddressNumbersResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
                } else {
                    getAddressNumbersResult.postValue(Resource.Error("No address number founds!", getAddressNumbersResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getAddressNumbersResult.postValue(Resource.Error("Getting address number error: ${e.message}", getAddressNumbersResult.value?.data))
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}