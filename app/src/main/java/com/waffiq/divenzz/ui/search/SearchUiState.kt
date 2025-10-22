package com.waffiq.divenzz.ui.search

import com.waffiq.divenzz.core.data.remote.response.EventResponse

sealed class SearchUiState {
  object Initial : SearchUiState()
  object Loading : SearchUiState()
  data class Success(val events: List<EventResponse>) : SearchUiState()
  data class Empty(val query: String) : SearchUiState()
  data class Error(val message: String) : SearchUiState()
}
