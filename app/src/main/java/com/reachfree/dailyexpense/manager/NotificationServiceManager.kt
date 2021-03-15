package com.reachfree.dailyexpense.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.reachfree.dailyexpense.receiver.NotificationReceiver
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.ACTION_EVERY_DAY_NOTIFICATION
import com.reachfree.dailyexpense.util.Constants.EVERY_DAY_NOTIFICATION
import timber.log.Timber
import java.util.*

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-15
 * Time: 오전 11:05
 */
class NotificationServiceManager(
    private val context: Context
) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setEverydayNotification() {
        Timber.d("setEverydayNotification")
        val alarm = Intent(ACTION_EVERY_DAY_NOTIFICATION).apply {
            putExtra(Constants.EXTRA_REQUEST_CDOE, EVERY_DAY_NOTIFICATION)
            setClass(context, NotificationReceiver::class.java)
        }

        val alarmRunning = PendingIntent.getBroadcast(
            context,
            EVERY_DAY_NOTIFICATION,
            alarm,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        if (!alarmRunning) {
            Timber.d("!alarmRunning")
            val now = Calendar.getInstance()
            val calSet = Calendar.getInstance()

            calSet[Calendar.HOUR_OF_DAY] = 20
            calSet[Calendar.MINUTE] = 0
            calSet[Calendar.SECOND] = 0
            calSet[Calendar.MILLISECOND] = 0

            if (!calSet.after(now)) {
                calSet.add(Calendar.DAY_OF_YEAR, 1)
            }

            val pendingIntent = PendingIntent.getBroadcast(context, EVERY_DAY_NOTIFICATION, alarm, 0)

            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calSet.timeInMillis,
                Constants.ONE_DAY,
                pendingIntent
            )
        } else {
            Timber.d("alarmRunning")
        }
    }

    fun cancelEverydayNotification() {
        Timber.d("cancelEverydayNotification")
        val alarm = Intent(ACTION_EVERY_DAY_NOTIFICATION).apply {
            setClass(context, NotificationReceiver::class.java)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, EVERY_DAY_NOTIFICATION, alarm, 0)
        pendingIntent.cancel()
        alarmManager?.cancel(pendingIntent)
    }

}