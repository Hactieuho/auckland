package com.liz.auckland.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.liz.auckland.BuildConfig
import com.liz.auckland.R
import com.liz.auckland.model.Address
import com.liz.auckland.util.KEY
import com.liz.auckland.util.getItem
import com.liz.auckland.util.getList
import com.liz.auckland.util.saveItem
import timber.log.Timber
import java.lang.Exception

class MainApplication : Application() {

    // Danh sach du lieu da luu
    var savedData = HashMap<String, Any?>()

    override fun onCreate() {
        super.onCreate()
        instant = this
        Timber.plant(Timber.DebugTree())
        createNotificationChannel()
        // Load danh sach du lieu da luu
        try {
            savedData = getFromSharedPre(KEY.SAVED_DATA)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Shared preference
    fun saveToSharePre(key: String, value: Any?) {
        var s = savedData
        if (s.isNullOrEmpty()) {
            s = HashMap()
        }
        s[key] = value
        savedData = s
        getSharedPreferences(getSharePreFileName(), 0).saveItem(KEY.SAVED_DATA, s)
    }
    private inline fun <reified T> getFromSharedPre(key: String): T? {
        return getSharedPreferences(getSharePreFileName(), 0).getItem(key)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getSharePreFileName(): String {
        return BuildConfig.APPLICATION_ID
    }

    companion object {
        lateinit var instant: MainApplication
        const val CHANNEL_ID = "auckland_channel"
    }
}