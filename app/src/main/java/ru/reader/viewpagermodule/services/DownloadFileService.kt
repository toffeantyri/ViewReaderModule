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
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookStateByName
import ru.reader.viewpagermodule.data.busines.repository.TAG_NEW_DOWNLOAD_SERVICE_STATE
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.adapters.LoadBookData

const val CHANNEL_ID = "CHANNEL_DOWNLOAD_SERVICE"
const val NOTIFICATION_ID = 123

class DownloadFileService : Service() {

    private val api = ApiProvider()
    private val iBinder: LocalBinder = LocalBinder()
    private var serviceStateByName = LoadingBookStateByName("", LoadingBookState.IDLE_LOAD)
        set(value) {
            field = value
            tellMeCurrentState()
        }

    private fun tellMeCurrentState() {
        sendBroadcast(Intent(BROADCAST_SERVICE_LOAD_STATE).putExtra(TAG_NEW_DOWNLOAD_SERVICE_STATE, serviceStateByName))
    }

    inner class LocalBinder : Binder() {
        fun getService(): DownloadFileService {
            return this@DownloadFileService
        }
    }

    private fun buildNotification(serviceStateIn: LoadingBookState): NotificationCompat.Builder {
        val notificationAction = android.R.drawable.stat_sys_download
        var notRemoveOnSwipe = true
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "LoadService"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)

        when (serviceStateIn) {
            LoadingBookState.LOADING -> {
            }
            LoadingBookState.SUCCESS_LOAD -> {
                notRemoveOnSwipe = false
            }
            LoadingBookState.LOAD_FAIL -> {
                notRemoveOnSwipe = false
            }
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(
                    androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                )
                .setDefaults(0)
                .setColor(Color.WHITE)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(notificationAction)
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
        val loadBookData = intent?.getSerializableExtra(BookListHelper.BOOK_LIST_DATA_FOR_LOAD) as LoadBookData
        Log.d("MyLog", "LOADSERVICE onStartCommand nameBook:  ${loadBookData.nameBook}")

        loadBookData.listOfUrls.forEach {
            Log.d("MyLog", "LOADSERVICE onStartCommand nameBook:  $it")
        }

        startForeground(NOTIFICATION_ID, buildNotification(serviceStateByName.state).build())

        //todo add to queue loads

        //todo call fun loading

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(3000)
                Log.d("MyLog", "LOADSERVICE onStartCommand success")
            }
            serviceStateByName = LoadingBookStateByName(loadBookData.nameBook, LoadingBookState.SUCCESS_LOAD)
            serviceStateByName = LoadingBookStateByName(loadBookData.nameBook, LoadingBookState.IDLE_LOAD)
            withContext(Dispatchers.IO) {
                delay(6000)
                Log.d("MyLog", "LOADSERVICE onStartCommand complite")
            }
            serviceStateByName = LoadingBookStateByName("", LoadingBookState.STATE_COMPLETE)
            //todo Выхов только когда все загрузки окончены

        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("MyLog", "LOADSERVICE onDestroy")
        super.onDestroy()
    }
}