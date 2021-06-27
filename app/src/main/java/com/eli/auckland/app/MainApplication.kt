package com.eli.auckland.app

import android.app.Application
import com.eli.auckland.BuildConfig
import com.eli.auckland.constant.getItem
import com.eli.auckland.constant.getList
import com.eli.auckland.constant.saveItem
import com.eli.auckland.model.Address
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instant = this
        Timber.plant(Timber.DebugTree())
    }

    // Shared preference
    fun saveToSharePre(key: String, value: Any?) {
        getSharedPreferences(getSharePreFileName(), 0).saveItem(key, value)
    }
    inline fun <reified T> getFromSharedPre(key: String): T? {
        return getSharedPreferences(getSharePreFileName(), 0).getItem(key)
    }
    fun getListFromSharedPre(key: String): ArrayList<Address>? {
        return getSharedPreferences(getSharePreFileName(), 0).getList(key)
    }
    fun containSharePre(key: String): Boolean {
        return getSharedPreferences(getSharePreFileName(), 0).contains(
            key
        )
    }

    fun getSharePreFileName(): String {
        return BuildConfig.APPLICATION_ID
    }

    companion object {
        lateinit var instant: MainApplication
    }
}