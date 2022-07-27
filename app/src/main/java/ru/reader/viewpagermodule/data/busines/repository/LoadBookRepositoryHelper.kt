package ru.reader.viewpagermodule.data.busines.repository

import android.content.*
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.services.DownloadFileService
import ru.reader.viewpagermodule.services.DownloadFileServiceBroadcastReceiver

const val BROADCAST_SERVICE_LOAD_STATE = "BROADCAST_SERVICE_LOAD_STATE"
const val TAG_NEW_DOWNLOAD_SERVICE_STATE = "TAG_NEW_DOWNLOAD_SERVICE_STATE "

class LoadBookRepositoryHelper : BaseRepository<LoadingBookState>() {

    private val context = APP_CONTEXT
    private var loadService: DownloadFileService? = null
    private var serviceBound = false
    private var serviceState: LoadingBookState = LoadingBookState.IDLE_LOAD


    fun loadBook(listUrl: ArrayList<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            registerBroadcastLoadService()
            val loadIntent = Intent(context, DownloadFileService::class.java)
            loadIntent.putStringArrayListExtra(BookListHelper.LIST_OF_URL_FOR_DOWNLOAD, listUrl)
            context.startForegroundService(loadIntent)
            context.bindService(loadIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DownloadFileService.LocalBinder
            loadService = binder.getService()
            serviceBound = true
            loadService!!.tellMeCurrentState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            loadService = null
        }
    }

    private fun registerBroadcastLoadService() {
        context.registerReceiver(
            broadcastReceiverNewServiceState,
            IntentFilter(BROADCAST_SERVICE_LOAD_STATE)
        )
    }

    private val broadcastReceiverNewServiceState = object : DownloadFileServiceBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MyLog", "onReceive")
            serviceState =
                intent?.getSerializableExtra(TAG_NEW_DOWNLOAD_SERVICE_STATE) as LoadingBookState
            when (serviceState) {
                LoadingBookState.SUCCESS_LOAD -> {
                    loadService?.stopForeground(true)
                    loadService?.stopSelf()
                    dataEmitter.onNext(LoadingBookState.SUCCESS_LOAD)
                    dataEmitter.onNext(LoadingBookState.IDLE_LOAD)
                }
                LoadingBookState.LOAD_FAIL -> {
                    dataEmitter.onNext(LoadingBookState.LOAD_FAIL)
                    dataEmitter.onNext(LoadingBookState.IDLE_LOAD)
                }
                LoadingBookState.IDLE_LOAD -> {
                    dataEmitter.onNext(LoadingBookState.IDLE_LOAD)
                }
                LoadingBookState.LOADING -> {
                    dataEmitter.onNext(LoadingBookState.LOADING)
                }
            }
        }
    }
}


