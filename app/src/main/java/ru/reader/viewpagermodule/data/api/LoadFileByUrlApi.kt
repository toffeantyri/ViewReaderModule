package ru.reader.viewpagermodule.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface LoadFileByUrlApi {

    @GET()
    @Streaming
    suspend fun getBookByUrl(@Url url : String) : Call<ResponseBody>

}