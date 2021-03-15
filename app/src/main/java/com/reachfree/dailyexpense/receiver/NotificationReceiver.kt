package com.reachfree.dailyexpense.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.LocalDatabase
import com.reachfree.dailyexpense.ui.base.SplashActivity
import com.reachfree.dailyexpense.ui.dashboard.DashboardActivity
import com.reachfree.dailyexpense.util.Constants
import com.reachfree.dailyexpense.util.Constants.ACTION_SHOW_ADD_EXPENSE
import com.reachfree.dailyexpense.util.Constants.EXTRA_REQUEST_CDOE
import com.reachfree.dailyexpense.util.Constants.NOTIFICATION_ID
import com.reachfree.dailyexpense.util.toMillis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-03-15
 * Time: 오전 11:09
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var database: LocalDatabase

    override fun onReceive(context: Context, intent: Intent) {
        if (Constants.EVERY_DAY_NOTIFICATION == intent.extras!!.get(EXTRA_REQUEST_CDOE)) {

            Timber.d("onReceive called!")
            database.databaseExecutor.execute {
                val startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toMillis()!!
                val endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toMillis()!!

                val count = database.transactionDao().getCountTodayTransaction(startDate, endDate)

                if (count == 0) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel(notificationManager)
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, SplashActivity::class.java).also {
                            it.action = ACTION_SHOW_ADD_EXPENSE
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val notificationBuilder =
                        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentTitle(context.resources.getString(R.string.app_name))
                            .setContentText("오늘 사용내역을 입력하셨나요^^?")
                            .setSmallIcon(R.drawable.logo)
                            .setContentIntent(pendingIntent)

                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }

//            val everyDayService = Intent(context, EveryDayService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.startForegroundService(context, everyDayService)
//            } else {
//                context.startService(everyDayService)
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}