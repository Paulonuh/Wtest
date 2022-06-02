package com.paulo.wtest.helper.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.paulo.wtest.R
import com.paulo.wtest.model.notification.Notification
import com.paulo.wtest.ui.main.MainActivity
import javax.inject.Inject

/**
 * Created by Paulo Henrique Teixeira.
 */

class NotificationHelper @Inject constructor() {

    companion object {

        const val SCREEN_SPLASH = 0
        const val SCREEN_BREAD = 1
        const val SCREEN_ZENDESK = 2

        //Channel
        private const val DEFAULT_CHANNEL_ID = "defaultChannel"
        private const val OTHER_CHANNEL_ID = "otherChannel"

        private const val DEFAULT_NAME = "Padrão"
        private const val OTHER_NAME = "Outras"

        private const val ID_NOTIFICATION_DEFAULT = 951

        const val EXTRA_NOTIFICATION = "notification"
        //Channel

        fun createNotification(
            context: Context,
            message: String,
            screen: Int? = SCREEN_SPLASH,
            data: Any? = null
        ) {
            val title = context.getString(R.string.app_name)
            val pendingIntent = createPendingIntent(context, screen!!, data)
            var notificationBuilder = buildBigNotification(
                context,
                title,
                message,
                DEFAULT_CHANNEL_ID,
                pendingIntent
            )

            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(ID_NOTIFICATION_DEFAULT, notificationBuilder.build())

            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                for (barNotification in notificationManager.activeNotifications) {
                    if (barNotification.id == ID_NOTIFICATION_DEFAULT) {

                        notificationManagerCompat.cancel(ID_NOTIFICATION_DEFAULT)
                        notificationBuilder = buildSilentBigNotification(
                            context,
                            title,
                            message,
                            OTHER_CHANNEL_ID,
                            pendingIntent
                        )

                        notificationManagerCompat.notify(
                            ID_NOTIFICATION_DEFAULT,
                            notificationBuilder.build()
                        )
                    }
                }
            }
        }

        private fun createPendingIntent(context: Context, screen: Int, data: Any?): PendingIntent {
            val intent = getIntent(context, screen, data)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        private fun getIntent(context: Context, screen: Int, data: Any?): Intent {
            val intent = Intent(context, MainActivity::class.java)

            when (screen) {
                SCREEN_BREAD -> {
                    val notification = Notification(screen, data as? Int)
                    intent.putExtra(EXTRA_NOTIFICATION, notification)
                }
                SCREEN_ZENDESK -> {
                    val notification = Notification(screen, data as? Int)
                    intent.putExtra(EXTRA_NOTIFICATION, notification)
                }
            }

            return intent
        }

        private fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        DEFAULT_CHANNEL_ID,
                        DEFAULT_NAME, NotificationManager.IMPORTANCE_HIGH
                    )
                )
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        OTHER_CHANNEL_ID,
                        OTHER_NAME, NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }

        private fun buildNotification(
            context: Context, title: String,
            message: String, channelId: String,
            pendingIntent: PendingIntent
        ): NotificationCompat.Builder {
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_launcher_background
            )

            createChannel(context)

            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setWhen(System.currentTimeMillis())
                .setTicker(context.resources.getString(R.string.new_notification))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.blue))
                .setLights(Color.BLUE, 1000, 10000)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
        }

        private fun buildBigNotification(
            context: Context,
            title: String,
            message: String,
            channelId: String,
            pendingIntent: PendingIntent
        ): NotificationCompat.Builder {

            val notificationBuilder = buildNotification(
                context,
                title,
                message,
                channelId,
                pendingIntent
            )

            return notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }

        private fun buildSilentBigNotification(
            context: Context,
            title: String,
            message: String,
            channelId: String,
            pendingIntent: PendingIntent
        ): NotificationCompat.Builder {
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_launcher_background
            )

            createChannel(
                context
            )

            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.gray_light))
                .setLights(Color.YELLOW, 1000, 10000)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setFullScreenIntent(pendingIntent, false)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }

    }
}