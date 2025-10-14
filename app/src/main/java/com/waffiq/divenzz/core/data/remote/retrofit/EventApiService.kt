package com.waffiq.divenzz.core.data.remote.retrofit

import com.waffiq.divenzz.core.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventApiService {

  @GET("/events")
  fun getAllEvent(
    @Query("active") active: Int,
  ): Call<EventResponse>
}
