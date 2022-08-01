package ru.reader.viewpagermodule.data.busines.repository

import android.content.*
import android.os.IBinder
import android.util.Log
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
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

class StateLoadingRepositoryHelper : BaseRepository<LoadingBookStateByName>() {

    private val context = APP_CONTEXT
    private var loadService: DownloadFileService? = null

    private var loadingIsComplete = true

    fun getStateEmitter(): Subject<LoadingBookStateByName> =
        if (loadingIsComplete) {
            stateEmitter = BehaviorSubject.create()
            stateEmitter
        } else stateEmitter


    fun loadBook(loadBookData: LoadBookData) {
        CoroutineScope(Dispatchers.IO).launch {
            registerBroadcastLoadService()
            val loadIntent = Intent(context, DownloadFileService::class.java)
            loadIntent.putExtra(BookListHelper.BOOK_LIST_DATA_FOR_LOAD, loadBookData)
            loadingIsComplete = false
            context.startForegroundService(loadIntent)
            context.bindService(loadIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    private val broadcastReceiverNewServiceState = object : DownloadFileServiceBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val intentState = intent?.getSerializableExtra(
                TAG_NEW_DOWNLOAD_SERVICE_STATE
            ) as LoadingBookStateByName
            Log.d("MyLog", "onReceive ${intentState.state}")

            when (intentState.state) {
                LoadingBookState.SUCCESS_LOAD -> {
                    stateEmitter.onNext(intentState)
                    stateEmitter.onNext(getUnnamedIdle())
                }
                LoadingBookState.LOAD_FAIL -> {
                    stateEmitter.onNext(intentState)
                    stateEmitter.onNext(getUnnamedIdle())
                }
                LoadingBookState.IDLE_LOAD -> {
                    stateEmitter.onNext(intentState)
                }
                LoadingBookState.LOADING -> {
                    stateEmitter.onNext(intentState)
                }
                LoadingBookState.STATE_COMPLETE -> {
                    loadingIsComplete = true
                    stateEmitter.onNext(intentState)
                    stateEmitter.onComplete()
                    loadService?.stopForeground(true)
                    loadService?.stopSelf()
                }
            }
        }

        fun getUnnamedIdle(): LoadingBookStateByName = LoadingBookStateByName("", LoadingBookState.IDLE_LOAD)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DownloadFileService.LocalBinder
            loadService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
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


