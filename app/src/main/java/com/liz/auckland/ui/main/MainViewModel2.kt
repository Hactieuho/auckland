package com.liz.auckland.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liz.auckland.data.RubbishRepository2
import com.liz.auckland.ui.main.di.MainData2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel2 @Inject constructor(
    val rubbishRepository: RubbishRepository2,
    // Main data
    val mainData2: MainData2
) : ViewModel() {
    // Thong tin rubbish
    val getRubbishInfoResult = rubbishRepository.getRubbishInfoResult
    // Them alarm
    val addAlarm = MutableLiveData<String?>(null)

    val alarm: (String?)->Unit = { addAlarm.postValue(it) }

}