package com.eli.auckland.data

import androidx.lifecycle.MutableLiveData
import com.eli.auckland.api.RubbishApi
import com.eli.auckland.app.MainApplication
import com.eli.auckland.util.KEY
import com.eli.auckland.model.Address
import com.eli.auckland.model.Rubbish
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
                    getTownCitiesResult.postValue(Resource.Error("Town cities not found", getTownCitiesResult.value?.data))
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
                val result = RubbishApi.api.getRoadName(locality)?.await()
                if (!result.isNullOrEmpty()) {
                    // Luu lai danh sach road name
                    getRoadNamesResult.postValue(Resource.Success(result.filter { s -> !s.isNullOrEmpty() }))
                } else {
                    getRoadNamesResult.postValue(Resource.Error("Road names not found", getRoadNamesResult.value?.data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getRoadNamesResult.postValue(Resource.Error("Getting road names error: ${e.message}", getRoadNamesResult.value?.data))
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}