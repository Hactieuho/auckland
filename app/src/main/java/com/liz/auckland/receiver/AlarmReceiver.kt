package com.liz.auckland.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.liz.auckland.R
import com.liz.auckland.app.MainApplication.Companion.CHANNEL_ID
import com.liz.auckland.util.KEY


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == KEY.ALARMS) {
            val title = intent.getStringExtra(KEY.NOTIFICATION_TITLE)
            Toast.makeText(context, title, Toast.LENGTH_LONG).show()
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
                .setContentText(title)
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