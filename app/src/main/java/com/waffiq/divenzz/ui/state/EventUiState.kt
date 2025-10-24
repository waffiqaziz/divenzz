package com.waffiq.divenzz.ui.state

import com.waffiq.divenzz.core.data.remote.response.EventResponse

sealed class EventUiState {
  object Loading : EventUiState()
  data class Success(val events: List<EventResponse>) : EventUiState()
  data class Error(val message: String) : EventUiState()
  object Empty : EventUiState()
}
