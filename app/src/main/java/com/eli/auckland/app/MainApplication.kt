package com.eli.auckland.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.eli.auckland.BuildConfig
import com.eli.auckland.R
import com.eli.auckland.model.Address
import com.eli.auckland.util.getItem
import com.eli.auckland.util.getList
import com.eli.auckland.util.saveItem
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instant = this
        Timber.plant(Timber.DebugTree())
        createNotificationChannel()
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

    fun getSharePreFileName(): String {
        return BuildConfig.APPLICATION_ID
    }

    companion object {
        lateinit var instant: MainApplication
        const val CHANNEL_ID = "auckland_channel"
    }
}