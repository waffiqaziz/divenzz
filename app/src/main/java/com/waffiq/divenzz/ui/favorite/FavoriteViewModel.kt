package com.waffiq.divenzz.ui.favorite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.database.EventEntity
import com.waffiq.divenzz.core.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : ViewModel() {

  private val mEventRepository: EventRepository = EventRepository(application)

  val getAllEvents: Flow<List<EventEntity>> = mEventRepository.getAllEvents()

  fun insert(event: EventEntity) {
    viewModelScope.launch {
      mEventRepository.insert(event)
    }
  }

  fun delete(event: EventEntity) {
    viewModelScope.launch {
      mEventRepository.delete(event)
    }
  }

  fun isFavorite(eventId: Int): Flow<Boolean> {
    return flow {
      val favoriteStatus = mEventRepository.isFavorite(eventId)
      emit(favoriteStatus)
    }
  }
}