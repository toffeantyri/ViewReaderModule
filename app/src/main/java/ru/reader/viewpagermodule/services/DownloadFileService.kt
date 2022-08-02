package ru.reader.viewpagermodule.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response
import ru.reader.viewpagermodule.data.api.ApiProviderForDownload
import ru.reader.viewpagermodule.data.busines.repository.BROADCAST_SERVICE_LOAD_STATE
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookState
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookStateByName
import ru.reader.viewpagermodule.data.busines.repository.TAG_NEW_DOWNLOAD_SERVICE_STATE
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.adapters.LoadBookData
import ru.reader.viewpagermodule.data.busines.storage.StorageHelper
import java.lang.Exception

const val CHANNEL_ID = "CHANNEL_DOWNLOAD_SERVICE"
const val NOTIFICATION_ID = 123

class DownloadFileService : Service() {

    private val api by lazy { ApiProviderForDownload() }
    private val sh by lazy { StorageHelper() }

    private val iBinder: LocalBinder = LocalBinder()
    private var serviceStateByTag = LoadingBookStateByName("", LoadingBookState.IDLE_LOAD)
        set(value) {
            field = value
            tellMeCurrentState()
        }

    private val queueLoadingFile: HashMap<String, LoadBookData> = hashMapOf()

    private fun tellMeCurrentState() {
        sendBroadcast(Intent(BROADCAST_SERVICE_LOAD_STATE).putExtra(TAG_NEW_DOWNLOAD_SERVICE_STATE, serviceStateByTag))
    }

    inner class LocalBinder : Binder() {
        fun getService(): DownloadFileService = this@DownloadFileService
    }

    override fun onBind(intent: Intent?): LocalBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyLog", "LOAD SERVICE onCreate")
        startForeground(NOTIFICATION_ID, buildNotification(serviceStateByTag.state).build())
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
            LoadingBookState.IDLE_LOAD -> {
                notRemoveOnSwipe = false
            }
            LoadingBookState.SUCCESS_LOAD -> {
                notRemoveOnSwipe = false
            }
            LoadingBookState.LOAD_FAIL -> {
                notRemoveOnSwipe = false
            }
            LoadingBookState.STATE_COMPLETE -> {
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

    private suspend fun loadBookByUrl(bookData: LoadBookData) {

        for (urlIndex in bookData.listOfUrls.indices) {
            Log.d("MyLog", bookData.listOfUrls[urlIndex])
        }

//        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//            Log.d("MyLog", "SERVICE CoroutineExceptionHandler : " + exception.message.toString())
//            serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.LOAD_FAIL)
//            throw exception
//        }

        val loading = CoroutineScope(Dispatchers.IO).async(/*exceptionHandler*/) {
            var response : Response<ResponseBody>? = null
            try {
                response = withContext(Dispatchers.IO) {
                    Log.d("MyLog", "loadBookByUrl inResponse")
                    api.provideLoaderFileByUrl(bookData.listOfUrls[0]).getBookByUrl(bookData.listOfUrls[0])
                }
            } catch (e : Exception){
                Log.d("MyLog", "loadBookByUrl catch inResponse ${e.message}")
                serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.LOAD_FAIL)
                return@async
            }
            if (!response.isSuccessful) {
                Log.d("MyLog", "loadBookByUrl: errorBody : ${response.errorBody()}")
                serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.LOAD_FAIL)
                return@async
            }

            if (response.isSuccessful) {
                Log.d("MyLog", "SERVICE response isSuccessful")
                val saveState: StorageHelper.Companion.StateSave = withContext(Dispatchers.IO) {
                    response.body()?.let {
                        sh.saveFileToPublicLocalPath(responseBody = it, bookData.defaultNameBook)
                    } ?: StorageHelper.Companion.StateSave.STATE_RESPONSE_BODY_NULL
                }
                Log.d("MyLog", "SERVICE response saveState $saveState")
                if (saveState != StorageHelper.Companion.StateSave.STATE_SAVED) {
                    serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.LOAD_FAIL)
                    return@async
                } else {
                    serviceStateByTag =
                        LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.SUCCESS_LOAD)
                    Log.d("MyLog", "SERVICE path saveFile ${StorageHelper.localPathFile.path} ")
                    Log.d(
                        "MyLog",
                        "SERVICE path/file save unzip ${StorageHelper.localPathFile.path}/${bookData.defaultNameBook}\" "
                    )
//                    val filePath = File("${StorageHelper.localPathFile.path}/${bookData.defaultNameBook}")
//                    sh.unzipFile(filePath, StorageHelper.localPathFile.path, bookData.defaultNameBook)
                }
            }
        }

        loading.await()
        Log.d("MyLog", "SERVICE loading coroutine isComplete : ${loading.isCompleted}")
        if (loading.isCompleted) {
            serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, LoadingBookState.STATE_COMPLETE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val loadBookData =
            intent?.getSerializableExtra(BookListHelper.BOOK_LIST_DATA_FOR_LOAD) as LoadBookData?
        Log.d("MyLog", "LOADSERVICE onStartCommand nameBook:  ${loadBookData?.defaultNameBook}")

        CoroutineScope(Dispatchers.IO).launch {
            if (loadBookData != null) {
                loadBookByUrl(loadBookData)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}