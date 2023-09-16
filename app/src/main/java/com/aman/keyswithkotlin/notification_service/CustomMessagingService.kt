package com.aman.keyswithkotlin.notification_service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.presentation.MainActivity
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray

class CustomMessagingService : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    private var notification: Notification? = null
    private var defaultSoundUri: Uri? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        println("Notification received: " + remoteMessage.messageId)
        println("Notification received: " + remoteMessage.data)
        //            //message without data payload
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();
//            notifyUser(title, message);

        // Check if message contains a data payload.
        var dataMap: Map<String?, String?> = HashMap()
        var noteType = ""

        if (remoteMessage.data.isNotEmpty()) {
            println("check1")
            noteType = remoteMessage.data["type"]!!
            dataMap = remoteMessage.data
        }
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        when (noteType) {
            BIG_TEXT -> bigTextNotification(dataMap)
            BIG_PIC -> bigPicNotification(dataMap)
            ACTIONS -> notificationActions(dataMap)
            DIRECT_REPLY -> directReply(dataMap)
            INBOX -> inboxTypeNotification(dataMap)
            MESSAGE -> messageTypeNotification(dataMap)
        }
    }

    //    private void notifyUser(String title, String messageBody, String type, String imagePath) {
    @OptIn(ExperimentalAnimationApi::class)
    private fun notifyUser(title: String, messageBody: String) {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId: String = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setColor(Color.YELLOW)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For android Oreo and above  notification channel is needed.
        val channel = NotificationChannel(
            channelId,
            "Fcm notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun bigTextNotification(dataMap: Map<String?, String?>) {
        val title = dataMap["title"]
        val message = dataMap["message"]
        val channelId: String = getString(R.string.default_notification_channel_id)
        val channelName = "FCMPush"
        val builder1: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager!!.createNotificationChannel(chan)
        val style: NotificationCompat.BigTextStyle = NotificationCompat.BigTextStyle()
        style.bigText(message)
        style.setSummaryText(title)
        builder1.setContentTitle("Message received")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
            .setColor(Color.BLUE)
            .setStyle(style)
        builder1.build()
        notification = builder1.getNotification()
        //        if (Build.VERSION.SDK_INT >= 26) {
//            startForeground(1, notification);
//        } else {
//            notificationManager.notify(1, notification);
//        }
        notificationManager!!.notify((0..2).random(), notification)
    }

    private fun bigPicNotification(dataMap: Map<String?, String?>) {
        val title = dataMap["title"]
        val message = dataMap["message"]
        val imageUrl = dataMap["imageUrl"]
        try {
            val channelId: String = getString(R.string.default_notification_channel_id)
            val channelName = "FCMPush"
            val builder2: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager!!.createNotificationChannel(chan)
            val style: NotificationCompat.BigPictureStyle = NotificationCompat.BigPictureStyle()
            style.setBigContentTitle(title)
            style.setSummaryText(message)
            style.bigPicture(
                Glide.with(this@CustomMessagingService).asBitmap().load(imageUrl).submit().get()
            )
            builder2.setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setColor(Color.GREEN)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
                .setStyle(style)
            builder2.build()
            notification = builder2.getNotification()
            notificationManager!!.notify(1, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun notificationActions(dataMap: Map<String?, String?>) {
        val title = dataMap["title"]
        val message = dataMap["message"]
        val channelId: String = getString(R.string.default_notification_channel_id)
        val channelName = "FCMPush"
        val builder3: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager!!.createNotificationChannel(chan)
        val intent1: Intent = Intent(this@CustomMessagingService, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this@CustomMessagingService,
            0,
            intent1,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cancelIntent: Intent = Intent(baseContext, NotificationReceiver::class.java)
        cancelIntent.putExtra("ID", 1)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder3.setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(Color.BLUE)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_google_logo, "VIEW", pendingIntent)
            .addAction(R.drawable.ic_google_logo, "DISMISS", cancelPendingIntent)
            .build()
        notification = builder3.getNotification()
        notificationManager!!.notify(1, notification)
    }

    @SuppressLint("LaunchActivityFromNotification")
    fun directReply(dataMap: Map<String?, String?>) {
        val title = dataMap["title"]
        val message = dataMap["message"]
        val channelId: String = getString(R.string.default_notification_channel_id)
        val channelName = "FCMPush"
        val builder4: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager!!.createNotificationChannel(chan)
        val cancelIntent: Intent = Intent(baseContext, NotificationReceiver::class.java)
        cancelIntent.putExtra("ID", 0)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val feedbackIntent: Intent =
            Intent(this@CustomMessagingService, NotificationReceiver::class.java)
        val feedbackPendingIntent = PendingIntent.getBroadcast(
            this@CustomMessagingService,
            100, feedbackIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val remoteInput = RemoteInput.Builder("DirectReplyNotification")
            .setLabel(message)
            .build()
        val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.ic_google_logo,
            "Write here...", feedbackPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()
        builder4.setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(feedbackPendingIntent)
            .addAction(action)
            .setColor(Color.RED)
            .addAction(R.drawable.ic_google_logo, "Cancel", cancelPendingIntent)
        builder4.build()
        notification = builder4.getNotification()
        notificationManager!!.notify(1, notification)
    }

    fun inboxTypeNotification(dataMap: Map<String?, String?>) {
        try {
            val title = dataMap["title"]
            val message = dataMap["message"]
            val jsonArray = JSONArray(dataMap["contentList"])
            val channelId: String = getString(R.string.default_notification_channel_id)
            val channelName = "FCMPush"
            val builder5: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager!!.createNotificationChannel(chan)
            val style: NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
            style.setSummaryText(message)
            style.setBigContentTitle(title)
            for (i in 0 until jsonArray.length()) {
                val emailName = jsonArray.getString(i)
                style.addLine(emailName)
            }
            builder5.setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
                .setStyle(style)
            builder5.build()
            notification = builder5.getNotification()
            notificationManager!!.notify(1, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun messageTypeNotification(dataMap: Map<String?, String?>?) {
        val channelId: String = getString(R.string.default_notification_channel_id)
        val channelName = "FCMPush"
        val builder6: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager!!.createNotificationChannel(chan)
        val style: NotificationCompat.MessagingStyle = NotificationCompat.MessagingStyle("Janhavi")
        style.addMessage("Is there any online tutorial for FCM?", 0, "member1")
        style.addMessage("Yes", 0, "member2")
        style.addMessage("How to use constraint layout?", 0, "member1")
        builder6.setSmallIcon(R.mipmap.ic_launcher_keys_privacy)
            .setColor(Color.RED)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.mipmap.ic_launcher_keys_privacy
                )
            )
            .setSound(defaultSoundUri)
            .setStyle(style)
            .setAutoCancel(true)
        builder6.build()
        notification = builder6.getNotification()
        notificationManager!!.notify(1, notification)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    companion object {
        const val BIG_TEXT = "BIGTEXT"
        const val BIG_PIC = "BIGPIC"
        const val ACTIONS = "ACTIONS"
        const val DIRECT_REPLY = "DIRECTREPLY"
        const val INBOX = "INBOX"
        const val MESSAGE = "MESSAGE"
    }
}