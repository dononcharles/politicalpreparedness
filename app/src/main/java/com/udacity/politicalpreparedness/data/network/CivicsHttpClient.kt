package com.udacity.politicalpreparedness.data.network

import com.udacity.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient : OkHttpClient() {

    companion object {

        private const val API_KEY = BuildConfig.API_CALL_KEY

        fun getClient(): OkHttpClient {
            return Builder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val url = original
                        .url
                        .newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build()

                    val request = original
                        .newBuilder()
                        .url(url)
                        .build()

                    chain.proceed(request)
                }
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    },
                )
                .build()
        }
    }
}
