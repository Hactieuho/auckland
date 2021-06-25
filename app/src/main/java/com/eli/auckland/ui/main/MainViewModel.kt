package com.eli.auckland.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eli.auckland.api.RubbishService
import com.eli.auckland.data.RubbishRepository

class MainViewModel : ViewModel() {
    val id = MutableLiveData("12342681539")
    val rubbish = RubbishRepository.instant.rubbish

    fun getRubbishInfo() {
        RubbishRepository.instant.getRubbish(id.value)
    }
}