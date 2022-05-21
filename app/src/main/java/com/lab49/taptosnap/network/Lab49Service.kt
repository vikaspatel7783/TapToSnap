package com.lab49.taptosnap.network

import com.lab49.taptosnap.model.Item
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Lab49Service {

    @GET("v1/item/list")
    suspend fun getItemList(): List<Item>

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