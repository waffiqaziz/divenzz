package com.waffiq.divenzz.core.data.remote.retrofit

import com.waffiq.divenzz.core.data.remote.response.DetailEventResponse
import com.waffiq.divenzz.core.data.remote.response.ListEventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {

  @GET("/events")
  suspend fun getAllEvent(
    @Query("active") active: Int,
  ): ListEventResponse

  @GET("/events/{eventId}")
  suspend fun getDetailEvent(
    @Path("eventId") eventId: Int,
  ): DetailEventResponse

  @GET("/events?active=-1")
  suspend fun search(
    @Query("q") query: String,
  ): ListEventResponse
}
