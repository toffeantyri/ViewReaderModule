package ru.reader.viewpagermodule.data.api

import retrofit2.Retrofit

class ApiProvider {

    private val emptyRetrofit by lazy { initApi() }

    private fun initApi() : Retrofit = Retrofit.Builder()
        .baseUrl("")
        .build()

    fun provideLoaderFileByUrl(): LoadFileByUrlApi = emptyRetrofit.create(LoadFileByUrlApi::class.java)

}