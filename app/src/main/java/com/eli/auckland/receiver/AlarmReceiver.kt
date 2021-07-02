package com.eli.auckland.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eli.auckland.R
import com.eli.auckland.app.MainApplication.Companion.CHANNEL_ID


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if (intent.action == "FOO_ACTION") {
            val fooString = intent.getStringExtra("KEY_FOO_STRING")
            Toast.makeText(context, fooString, Toast.LENGTH_LONG).show()
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val mp = MediaPlayer.create(context.applicationContext, defaultSoundUri)
//            mp.start()
//            intent.putExtra("mp", mp.audioSessionId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Auckland")
                .setAutoCancel(true)
                .setContentText(fooString)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(0, builder.build())
            }
        }
    }
}