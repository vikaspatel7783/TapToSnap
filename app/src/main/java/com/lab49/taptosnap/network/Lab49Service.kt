package com.lab49.taptosnap.network

import com.lab49.taptosnap.model.ItemResponse
import com.lab49.taptosnap.model.ItemMatchResponse
import com.lab49.taptosnap.model.ItemMatchRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Lab49Service {

    @GET("v1/item/list")
    suspend fun getItemList(): List<ItemResponse>

    @POST("v1/item/image")
    suspend fun matchItem(@Body itemMatchRequest: ItemMatchRequest): ItemMatchResponse

    companion object {
        private const val BASE_URL = "https://taptosnap.nonprod.kube.lab49cloud.com/"

        fun create(): Lab49Service {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Lab49Service::class.java)
        }
    }
}