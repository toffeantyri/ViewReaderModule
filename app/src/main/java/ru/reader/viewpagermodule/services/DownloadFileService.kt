package ru.reader.viewpagermodule.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import ru.reader.viewpagermodule.data.api.ApiProvider
import ru.reader.viewpagermodule.data.busines.repository.BROADCAST_SERVICE_LOAD_STATE
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookState
import ru.reader.viewpagermodule.data.busines.repository.TAG_NEW_DOWNLOAD_SERVICE_STATE
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper

const val CHANNEL_ID = "CHANNEL_DOWNLOAD_SERVICE"
const val NOTIFICATION_ID = 123

class DownloadFileService : Service() {

    private val api = ApiProvider()
    private val iBinder: LocalBinder = LocalBinder()
    private var serviceState = LoadingBookState.IDLE_LOAD
        set(value) {
            field = value
            tellMeCurrentState()
        }

    fun tellMeCurrentState() {
        sendBroadcast(Intent(BROADCAST_SERVICE_LOAD_STATE).putExtra(TAG_NEW_DOWNLOAD_SERVICE_STATE, serviceState))
    }


    inner class LocalBinder : Binder() {
        fun getService(): DownloadFileService {
            return this@DownloadFileService
        }
    }

    private fun buildNotification(serviceStateIn: LoadingBookState): NotificationCompat.Builder {
        var notificationAction = android.R.drawable.stat_sys_download
        var notRemoveOnSwipe = true
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "LoadService"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        if (serviceStateIn == LoadingBookState.LOADING) {

        } else if (serviceStateIn == LoadingBookState.SUCCESS_LOAD) {

        } else if (serviceStateIn == LoadingBookState.LOAD_FAIL) {

        }

        val cancelDrawable = android.R.drawable.ic_menu_close_clear_cancel

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(
                    androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                )
                .setDefaults(0)
                .setColor(Color.GREEN)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(notificationAction)
                //.addAction(cancelDrawable, "Cancel", playbackAction)
                .setOngoing(notRemoveOnSwipe)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        return notificationBuilder
    }


    override fun onBind(intent: Intent?): LocalBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyLog", "LOADSERVICE onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val urlList = intent?.getStringArrayListExtra(BookListHelper.LIST_OF_URL_FOR_DOWNLOAD)
        urlList?.forEach { Log.d("MyLog", "LOADSERVICE onStartCommand $it") }

        //todo get file from api

        startForeground(NOTIFICATION_ID, buildNotification(serviceState).build())

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(6000)
                Log.d("MyLog", "delay 10")
            }

            serviceState = LoadingBookState.SUCCESS_LOAD
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("MyLog", "LOADSERVICE onDestroy")
        super.onDestroy()
    }
}