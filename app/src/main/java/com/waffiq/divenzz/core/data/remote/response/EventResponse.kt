package com.waffiq.divenzz.core.data.remote.response

import com.squareup.moshi.Json

data class EventResponse(

  @Json(name="summary")
  val summary: String? = null,

  @Json(name="mediaCover")
  val mediaCover: String? = null,

  @Json(name="registrants")
  val registrants: Int? = null,

  @Json(name="imageLogo")
  val imageLogo: String? = null,

  @Json(name="link")
  val link: String? = null,

  @Json(name="description")
  val description: String? = null,

  @Json(name="ownerName")
  val ownerName: String? = null,

  @Json(name="cityName")
  val cityName: String? = null,

  @Json(name="quota")
  val quota: Int? = null,

  @Json(name="name")
  val name: String? = null,

  @Json(name="id")
  val id: Int,

  @Json(name="beginTime")
  val beginTime: String? = null,

  @Json(name="endTime")
  val endTime: String? = null,

  @Json(name="category")
  val category: String? = null
)
