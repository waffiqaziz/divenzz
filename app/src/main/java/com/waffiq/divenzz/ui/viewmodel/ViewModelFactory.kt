package com.waffiq.divenzz.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.core.data.di.Injection
import com.waffiq.divenzz.core.data.repository.EventRepository
import com.waffiq.divenzz.ui.detail.DetailEventViewModel
import com.waffiq.divenzz.ui.favorite.FavoriteViewModel
import com.waffiq.divenzz.ui.past.PastEventViewModel
import com.waffiq.divenzz.ui.search.SearchViewModel
import com.waffiq.divenzz.ui.settings.SettingViewModel
import com.waffiq.divenzz.ui.upcoming.UpcomingEventViewModel

class ViewModelFactory private constructor(
  private val repository: EventRepository,
  private val pref: SettingPreferences,
) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
      return SettingViewModel(pref) as T
    } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
      return FavoriteViewModel(repository) as T
    } else if (modelClass.isAssignableFrom(UpcomingEventViewModel::class.java)) {
      return UpcomingEventViewModel(repository) as T
    } else if (modelClass.isAssignableFrom(PastEventViewModel::class.java)) {
      return PastEventViewModel(repository) as T
    } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
      return SearchViewModel(repository) as T
    } else if (modelClass.isAssignableFrom(DetailEventViewModel::class.java)) {
      return DetailEventViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
  }

  companion object {
    @Volatile
    private var instance: ViewModelFactory? = null
    fun getInstance(context: Context): ViewModelFactory =
      instance ?: synchronized(this) {
        instance ?: ViewModelFactory(
          Injection.provideEventRepository(context),
          Injection.provideSettingPreferences(context)
        )
      }.also { instance = it }
  }
}