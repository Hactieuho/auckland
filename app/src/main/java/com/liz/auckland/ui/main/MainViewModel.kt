package com.liz.auckland.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liz.auckland.data.RubbishRepository
import com.liz.auckland.ui.main.di.MainData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val rubbishRepository: RubbishRepository,
    // Main data
    val mainData: MainData
) : ViewModel() {
    // Thong tin rubbish
    val getRubbishInfoResult = rubbishRepository.getRubbishInfoResult
    // Them alarm
    val addAlarm = MutableLiveData<String?>(null)

    val alarm: (String?)->Unit = { addAlarm.postValue(it) }

}