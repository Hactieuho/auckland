package com.eli.auckland.data

import androidx.lifecycle.MutableLiveData
import com.eli.auckland.api.RubbishApi
import com.eli.auckland.app.MainApplication
import com.eli.auckland.constant.KEY
import com.eli.auckland.model.Address
import com.eli.auckland.model.ApiResult
import com.eli.auckland.model.Rubbish
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.await
import java.lang.Exception

class RubbishRepository {

    // Danh sach dia chi
    val addressList = MutableLiveData<List<Address>>(null)
    val getAddressListResult = MutableLiveData<ApiResult?>(null)
    // Dia chi hien tai
    val currentAddress = MutableLiveData<Address?>(null)
    // Thong tin dia chi hien tai
    val rubbish = MutableLiveData<Rubbish>(null)
    val getRubbishResult = MutableLiveData<ApiResult?>(null)
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

    // Lay danh sach dia chi
    fun getLocations() {
        ioScope.launch {
            try {
                getAddressListResult.postValue(ApiResult.LOADING)
                val result = RubbishApi.api.getLocations()?.await()
                if (result?.isSuccessful() == true) {
                    addressList.postValue(result.address)
                    // Luu lai danh sach dia chi
                    MainApplication.instant.saveToSharePre(KEY.ADDRESS_LIST, result.address)
                    getAddressListResult.postValue(ApiResult.SUCCESS)
                } else {
                    getAddressListResult.postValue(ApiResult.ERROR)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getAddressListResult.postValue(ApiResult.ERROR)
            }
        }
    }

    fun getRubbish(an: String?) {
        ioScope.launch {
            try {
                getRubbishResult.postValue(ApiResult.LOADING)
                val result = RubbishApi.api.getRubbish(an)?.await()
                if (result != null) {
                    rubbish.postValue(result)
                    // Luu lai address hien tai
                    MainApplication.instant.saveToSharePre(KEY.CURRENT_ADDRESS, currentAddress.value)
                    getRubbishResult.postValue(ApiResult.SUCCESS)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getRubbishResult.postValue(ApiResult.ERROR)
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}