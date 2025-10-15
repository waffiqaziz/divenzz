package com.waffiq.divenzz.core.data.remote.response

import com.squareup.moshi.Json

data class DetailEventResponse(

	@Json(name="error")
	val error: Boolean? = null,

	@Json(name="message")
	val message: String? = null,

	@Json(name="event")
	val event: EventResponse? = null
)
