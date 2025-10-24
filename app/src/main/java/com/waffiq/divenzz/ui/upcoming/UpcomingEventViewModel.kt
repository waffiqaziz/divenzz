package com.waffiq.divenzz.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.repository.EventRepository
import com.waffiq.divenzz.ui.state.EventUiState
import com.waffiq.divenzz.utils.NetworkResult
import kotlinx.coroutines.launch

class UpcomingEventViewModel(
  private val repository: EventRepository,
) : ViewModel() {

  private val _uiState = MutableLiveData<EventUiState>()
  val uiState: LiveData<EventUiState> = _uiState

  init {
    getUpcomingEvents()
  }

  fun getUpcomingEvents() {
    viewModelScope.launch {
      repository.getUpcomingEvents()
        .collect { result ->
          _uiState.value = when (result) {
            is NetworkResult.Loading -> EventUiState.Loading
            is NetworkResult.Success -> {
              if (result.data.isEmpty()) {
                EventUiState.Empty
              } else {
                EventUiState.Success(result.data)
              }
            }
            is NetworkResult.Error -> EventUiState.Error(result.message)
          }
        }
    }
  }
}
