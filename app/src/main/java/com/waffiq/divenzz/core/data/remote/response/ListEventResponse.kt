package com.waffiq.divenzz.core.data.remote.response

import com.squareup.moshi.Json

data class ListEventResponse(

	@Json(name="listEvents")
	val listEvents: List<EventResponse?>? = null,

	@Json(name="error")
	val error: Boolean? = null,

	@Json(name="message")
	val message: String? = null
)
