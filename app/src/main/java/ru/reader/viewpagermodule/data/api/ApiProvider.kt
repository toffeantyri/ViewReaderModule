package ru.reader.viewpagermodule.data.api

import retrofit2.Retrofit

class ApiProviderForDownload() {

    private val emptyRetrofit by lazy { initApi() }

    private fun initApi(): Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .build()

    fun provideLoaderFileApi(): LoadFileByUrlApi = emptyRetrofit.create(LoadFileByUrlApi::class.java)


}