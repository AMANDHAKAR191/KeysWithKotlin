package com.aman.keys.notification_service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.aman.keys.R

class NotificationReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (intent.hasExtra("ID")) {
            val noteId = intent.getIntExtra("ID", 1)
            notificationManager!!.cancel(noteId)
        } else {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                val feedback = remoteInput.getCharSequence("DirectReplyNotification")
                val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher_keys_lock)
                    .setContentTitle("Thank you for your feedback!!!")
                notificationManager!!.notify(1, mBuilder.build())
            }
        }
    }
}