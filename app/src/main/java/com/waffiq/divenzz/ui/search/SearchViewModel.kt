package com.waffiq.divenzz.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.repository.EventRepository
import com.waffiq.divenzz.utils.NetworkResult
import kotlinx.coroutines.launch

class SearchViewModel(
  private val repository: EventRepository,
) : ViewModel() {

  private val _uiState = MutableLiveData<SearchUiState>()
  val uiState: LiveData<SearchUiState> = _uiState

  init {
    _uiState.value = SearchUiState.Initial
  }

  fun search(query: String) {
    if (query.isBlank()) {
      _uiState.value = SearchUiState.Initial
      return
    }

    viewModelScope.launch {
      repository.searchEvents(query)
        .collect { result ->
          _uiState.value = when (result) {
            is NetworkResult.Loading -> SearchUiState.Loading
            is NetworkResult.Success -> {
              if (result.data.isEmpty()) {
                SearchUiState.Empty(query)
              } else {
                SearchUiState.Success(result.data)
              }
            }
            is NetworkResult.Error -> SearchUiState.Error(result.message)
          }
        }
    }
  }
}
