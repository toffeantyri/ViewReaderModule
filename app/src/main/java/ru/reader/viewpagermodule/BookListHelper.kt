package ru.reader.viewpagermodule

import android.os.Environment
import android.util.Log

class BookListHelper() {

    private val context by lazy { APP_CONTEXT }

    /** get list files names from cache and add to list */
    fun getListFB2NameFromCache(): HashSet<String> {
        val listFilesName = hashSetOf<String>()
        App.getDirCache.list()?.let { listFiles ->
            for (name in listFiles) {
                //Log.d("MyLog", "cache file name : ${name}")
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    listFilesName.add(name)
                }
            }
        }
        listFilesName.forEach { Log.d("MyLog", "cache file name - $it") }
        return listFilesName
    }

    /** get list files names from asset and add to list */
    fun getListFB2NameFromAsset(): HashSet<String> {
        val listFilesName = hashSetOf<String>()
        App.getAssetManager.list("")?.let { listNames ->
            for (name in listNames) {
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    listFilesName.add(name)
                }
            }
        }
        listFilesName.forEach { Log.d("MyLog", "asset file name - $it") }
        return listFilesName
    }

    /** get list files names from download and add to list */
    fun getListFB2NameFromDownload(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOWNLOADS)

        listFileNames.forEach { Log.d("MyLog", "all files DOWNLOADS : $it") }
        return listFileNames
    }

    /** get list files names from document and add to list */
    fun getListFB2NameFromDocument(): HashSet<String> {
        var listFileNames: HashSet<String> = hashSetOf()

        listFileNames = listFileNames.getEnvironmentPathListNames(Environment.DIRECTORY_DOCUMENTS)

        listFileNames.forEach { Log.d("MyLog", "all files DOCUMENTS : $it") }
        return listFileNames
    }

    private fun HashSet<String>.getEnvironmentPathListNames(path: String): HashSet<String> {
        val hashSet = this
        Environment.getExternalStoragePublicDirectory(path)?.list()?.let { arrayNames ->
            for (name in arrayNames) {
                Log.d("MyLog", "$path file name : $name")
                val listName = name.split(".")
                if (listName.size >= 2 && listName.last() == "fb2") {
                    hashSet.add(name)
                }
            }
        }
        return hashSet
    }


}