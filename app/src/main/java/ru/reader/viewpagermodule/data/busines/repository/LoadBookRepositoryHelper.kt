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
import ru.reader.viewpagermodule.view.adapters.LoadBookData

const val BROADCAST_SERVICE_LOAD_STATE = "BROADCAST_SERVICE_LOAD_STATE"
const val TAG_NEW_DOWNLOAD_SERVICE_STATE = "TAG_NEW_DOWNLOAD_SERVICE_STATE "

class LoadBookRepositoryHelper : BaseRepository<LoadingBookStateByName>() {

    private val context = APP_CONTEXT
    private var loadService: DownloadFileService? = null
    private var serviceBound = false


    fun loadBook(loadBookData: LoadBookData) {
        CoroutineScope(Dispatchers.IO).launch {
            registerBroadcastLoadService()
            val loadIntent = Intent(context, DownloadFileService::class.java)
            loadIntent.putExtra(BookListHelper.BOOK_LIST_DATA_FOR_LOAD, loadBookData)
            context.startForegroundService(loadIntent)
            context.bindService(loadIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    private val broadcastReceiverNewServiceState = object : DownloadFileServiceBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val intentState = intent?.getSerializableExtra(
                TAG_NEW_DOWNLOAD_SERVICE_STATE
            ) as LoadingBookStateByName
            //Log.d("MyLog", "onReceive ${intentState.state}")

            when (intentState.state) {
                LoadingBookState.SUCCESS_LOAD -> {
                    dataEmitter.onNext(intentState)
                    dataEmitter.onNext(getIdle())
                }
                LoadingBookState.LOAD_FAIL -> {
                    dataEmitter.onNext(intentState)
                    dataEmitter.onNext(getIdle())
                }
                LoadingBookState.IDLE_LOAD -> {
                    dataEmitter.onNext(getIdle())
                    loadService?.stopForeground(true)
                    loadService?.stopSelf()
                }
                LoadingBookState.LOADING -> {
                    dataEmitter.onNext(intentState)
                }
            }
        }

        fun getIdle(): LoadingBookStateByName = LoadingBookStateByName("", LoadingBookState.IDLE_LOAD)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DownloadFileService.LocalBinder
            loadService = binder.getService()
            serviceBound = true
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

}


