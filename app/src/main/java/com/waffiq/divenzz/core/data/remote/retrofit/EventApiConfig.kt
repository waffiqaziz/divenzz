package com.waffiq.divenzz.core.data.remote.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffiq.divenzz.utils.Constant.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class EventApiConfig {

  companion object {

    fun getApiService(): EventApiService {
      val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

      val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

      val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

      val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

      return retrofit.create(EventApiService::class.java)
    }
  }
}
