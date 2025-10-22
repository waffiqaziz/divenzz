package com.waffiq.divenzz.ui.detail

import com.waffiq.divenzz.core.data.remote.response.EventResponse

sealed class DetailEventUiState {
  object Loading : DetailEventUiState()
  data class Success(val event: EventResponse) : DetailEventUiState()
  data class Error(val message: String) : DetailEventUiState()
}
