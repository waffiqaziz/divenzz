package com.waffiq.divenzz.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.database.EventEntity
import com.waffiq.divenzz.core.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FavoriteViewModel(
  private val repository: EventRepository,
) : ViewModel() {

  val getAllEvents: Flow<List<EventEntity>> = repository.getAllEvents()

  fun insert(event: EventEntity) {
    viewModelScope.launch {
      repository.insert(event)
    }
  }

  fun delete(event: EventEntity) {
    viewModelScope.launch {
      repository.delete(event)
    }
  }

  fun isFavorite(eventId: Int): Flow<Boolean> {
    return flow {
      val favoriteStatus = repository.isFavorite(eventId)
      emit(favoriteStatus)
    }
  }
}