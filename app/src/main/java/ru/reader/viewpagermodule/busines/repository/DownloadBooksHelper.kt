package ru.reader.viewpagermodule.busines.repository

import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.reader.viewpagermodule.APP_CONTEXT
import java.util.concurrent.TimeUnit

class DownloadBooksHelper(workerParams: WorkerParameters) : Worker(APP_CONTEXT, workerParams) {

    override fun doWork(): Result {
        Log.d("MyLog", "LoadBook start")
        TimeUnit.SECONDS.sleep(10)
        Log.d("MyLog", "LoadBook end")
        return Result.success()
    }

}