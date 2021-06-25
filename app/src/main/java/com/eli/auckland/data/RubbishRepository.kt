package com.eli.auckland.data

import androidx.lifecycle.MutableLiveData
import com.eli.auckland.api.RubbishApi
import com.eli.auckland.api.RubbishApiTest
import com.eli.auckland.model.Rubbish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.await
import timber.log.Timber

class RubbishRepository {
    val rubbish = MutableLiveData<Rubbish>(null)
    val job = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun getRubbish(an: String?) {
        ioScope.launch {
//            val result = RubbishApi.api.getRubbish(an)?.await()
            val result = RubbishApiTest.api.getRubbish()?.await()
            if (result != null) {
                rubbish.postValue(result)
            }
        }
    }

    companion object {
        val instant = RubbishRepository()
    }
}