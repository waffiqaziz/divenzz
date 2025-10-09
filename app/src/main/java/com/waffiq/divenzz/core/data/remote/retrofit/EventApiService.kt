package com.waffiq.divenzz.core.data.remote.retrofit

import com.waffiq.divenzz.core.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET

interface EventApiService {

  @GET("/events")
  fun getAllEvent(): Call<EventResponse>
}
