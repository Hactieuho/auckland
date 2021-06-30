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
    // Danh sach dia chi
    val addressList = MutableLiveData<List<Address>>(null)
    // Dia chi hien tai
    val currentAddress = MutableLiveData<Address?>(null)
    // Lay danh sach dia chi
    val getAddressListResult = MutableLiveData<Resource<List<Address>?>>()
    // Thong tin dia chi hien tai
    val rubbish = MutableLiveData<Rubbish>(null)
    // Lay thong tin dia chi hien tai
    val getRubbishResult = MutableLiveData<Resource<Rubbish>>()
    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        // Lay danh sach dia chi da luu
        val addresses = MainApplication.instant.getListFromSharedPre(KEY.ADDRESS_LIST)
        if (addresses != null) {
            addressList.postValue(addresses)
        }
        // Lay address da luu
        val address = MainApplication.instant.getFromSharedPre<Address>(KEY.CURRENT_ADDRESS)
        if (address != null) {
            currentAddress.postValue(address)
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

    fun getRubbish(an: String?) {
        ioScope.launch {
            try {
                val result = RubbishApi.api.getRubbish(an)?.await()
                if (result != null) {
                    // Luu lai address hien tai
                    getRubbishResult.postValue(Resource.Success(result))
                    rubbish.postValue(result)
                    MainApplication.instant.saveToSharePre(KEY.CURRENT_ADDRESS, currentAddress.value)
                }
            } catch (e: Exception) {
                getRubbishResult.postValue(Resource.Error("Get rubbish info error: ${e.message}"))
                e.printStackTrace()
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}