package ru.reader.viewpagermodule.data.api

import retrofit2.Retrofit

class ApiProviderForDownload() {

    private val emptyRetrofit by lazy { initApi("") }

    private fun initApi(url: String): Retrofit = Retrofit.Builder()
        .baseUrl("$url/")
        .build()

    fun provideLoaderFile(): LoadFileByUrlApi = emptyRetrofit.create(LoadFileByUrlApi::class.java)

    fun provideLoaderFileByUrl(url: String): LoadFileByUrlApi = initApi(url).create(LoadFileByUrlApi::class.java)


}