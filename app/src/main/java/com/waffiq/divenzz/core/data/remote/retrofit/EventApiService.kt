package com.waffiq.divenzz.core.data.remote.retrofit

import com.waffiq.divenzz.core.data.remote.response.DetailEventResponse
import com.waffiq.divenzz.core.data.remote.response.ListEventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {

  @GET("/events")
  fun getAllEvent(
    @Query("active") active: Int,
  ): Call<ListEventResponse>

  @GET("/events/{eventId}")
  fun getEventDetail(
    @Path("eventId") eventId: Int,
  ): Call<DetailEventResponse>

  @GET("/events?active=-1")
  fun search(
    @Query("q") query: String,
  ): Call<ListEventResponse>
}
