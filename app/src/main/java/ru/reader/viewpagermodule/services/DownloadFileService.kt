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
import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.data.api.ApiProviderForDownload
import ru.reader.viewpagermodule.data.busines.repository.BROADCAST_SERVICE_LOAD_STATE
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookState
import ru.reader.viewpagermodule.data.busines.repository.LoadingBookStateByName
import ru.reader.viewpagermodule.data.busines.repository.TAG_NEW_DOWNLOAD_SERVICE_STATE
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.view.models.LoadBookData
import ru.reader.viewpagermodule.data.busines.storage.StorageHelper
import java.io.File
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
    }

    private fun buildNotification(serviceStateIn: LoadingBookState): NotificationCompat.Builder {
        val notificationAction = android.R.drawable.stat_sys_download
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val textList = queueLoadingFile.map { it.key + ", " }
        val name = "LoadService"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
        val notRemoveOnSwipe = when (serviceStateIn) {
            LoadingBookState.LOADING -> true
            LoadingBookState.IDLE_LOAD -> false
            LoadingBookState.SUCCESS_LOAD -> false
            LoadingBookState.LOAD_FAIL -> false
            LoadingBookState.STATE_COMPLETE -> false
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(
                    androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                )
                .setDefaults(0)
                .setColor(Color.LTGRAY)
                .setColorized(true)
                .setContentTitle(getString(R.string.toast_loading))
                .setContentText(textList.toString())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(notificationAction)
                .setOngoing(notRemoveOnSwipe)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        return notificationBuilder
    }

    private suspend fun loadBookByUrl(bookData: LoadBookData) {
        addToQueueLoadings(bookData)
        buildNotification(LoadingBookState.LOADING)
        lateinit var currentState: LoadingBookState
        for (urlIndex in bookData.listOfUrls.indices) {
            Log.d("MyLog", bookData.listOfUrls[urlIndex])
            val url = bookData.listOfUrls[urlIndex]
            val willFileName = url.substring(url.lastIndexOf("/") + 1)
            val loading = CoroutineScope(Dispatchers.IO).async {
                val response: Response<ResponseBody>
                try {
                    response = withContext(Dispatchers.IO) {
                        api.provideLoaderFileApi().getBookByUrl(bookData.listOfUrls[urlIndex])
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "SERVICE: ResponseApi Error: ${e.message}")
                    removeFromQueueLoadings(bookData)
                    currentState = LoadingBookState.LOAD_FAIL
                    return@async
                }

                if (!response.isSuccessful) {
                    Log.d("MyLog", "SERVICE: Response notSuccess errorBody : ${response.errorBody()}")
                    currentState = LoadingBookState.LOAD_FAIL
                    return@async
                }

                if (response.isSuccessful) {
                    val saveState: StorageHelper.Companion.StateSave = withContext(Dispatchers.IO) {
                        response.body()?.let {
                            sh.saveFileToPublicLocalPath(responseBody = it, willFileName)
                        } ?: StorageHelper.Companion.StateSave.STATE_RESPONSE_BODY_NULL
                    }
                    if (saveState != StorageHelper.Companion.StateSave.STATE_SAVED) {
                        currentState = LoadingBookState.LOAD_FAIL
                        return@async
                    } else {
                        try {
                            withContext(Dispatchers.IO) {
                                val filePath = File("${StorageHelper.localPathFile.path}/$willFileName")
                                sh.unzipFb2File(filePath, bookData.defaultNameBook)
                            }
                            currentState = LoadingBookState.SUCCESS_LOAD
                        } catch (e: Exception) {
                            Log.d("MyLog", "SERVICE: loadBookByUrl UNZIP catch  ${e.message}")
                            removeFromQueueLoadings(bookData)
                            currentState = LoadingBookState.LOAD_FAIL
                            //throw e
                            return@async
                        } finally {
                            File("${StorageHelper.localPathFile.path}/$willFileName").delete()
                        }
                    }
                }
            }
            loading.await()
            Log.d("MyLog", "SERVICE loading isComplete : ${loading.isCompleted} state : $currentState")
            if (currentState == LoadingBookState.SUCCESS_LOAD) {
                serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, currentState)
                removeFromQueueLoadings(bookData)
                buildNotification(LoadingBookState.LOADING)
                checkServiceComplete()
                break
            }
            if (urlIndex == (bookData.listOfUrls.size - 1)) {
                serviceStateByTag = LoadingBookStateByName(bookData.defaultNameBook, currentState)
                removeFromQueueLoadings(bookData)
                buildNotification(LoadingBookState.LOADING)
                checkServiceComplete()
                break
            } else continue
        }
    }

    private fun checkServiceComplete() {
        if (queueLoadingFile.isEmpty()) serviceStateByTag = LoadingBookStateByName("", LoadingBookState.STATE_COMPLETE)
    }

    private fun removeFromQueueLoadings(loadBookData: LoadBookData) {
        val key = loadBookData.defaultNameBook
        if (queueLoadingFile.containsKey(key)) queueLoadingFile.remove(key)
    }

    private fun addToQueueLoadings(loadBookData: LoadBookData) {
        val key = loadBookData.defaultNameBook
        if (!queueLoadingFile.containsKey(key)) queueLoadingFile[key] = loadBookData
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val loadBookData =
            intent?.getSerializableExtra(BookListHelper.BOOK_LIST_DATA_FOR_LOAD) as LoadBookData?
        Log.d("MyLog", "LOADS SERVICE onStartCommand nameBook:  ${loadBookData?.defaultNameBook}")

        startForeground(NOTIFICATION_ID, buildNotification(serviceStateByTag.state).build())

        CoroutineScope(Dispatchers.IO).launch {
            if (loadBookData != null) {
                loadBookByUrl(loadBookData)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}