package com.waffiq.divenzz.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.repository.EventRepository
import com.waffiq.divenzz.utils.NetworkResult
import kotlinx.coroutines.launch

class DetailEventViewModel(
  private val repository: EventRepository
) : ViewModel() {

  private val _uiState = MutableLiveData<DetailEventUiState>()
  val uiState: LiveData<DetailEventUiState> = _uiState

  fun getDetailEvent(eventId: Int) {
    viewModelScope.launch {
      repository.getDetailEvent(eventId)
        .collect { result ->
          _uiState.value = when (result) {
            is NetworkResult.Loading -> DetailEventUiState.Loading
            is NetworkResult.Success -> DetailEventUiState.Success(result.data)
            is NetworkResult.Error -> DetailEventUiState.Error(result.message)
          }
        }
    }
  }

  fun retry(eventId: Int) {
    getDetailEvent(eventId)
  }
}
