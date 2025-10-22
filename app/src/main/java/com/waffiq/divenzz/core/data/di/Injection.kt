package com.waffiq.divenzz.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.waffiq.divenzz.core.data.database.EventDatabase
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.core.data.remote.retrofit.EventApiConfig
import com.waffiq.divenzz.core.data.repository.EventRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
  fun provideEventRepository(context: Context): EventRepository {
    val database = EventDatabase.getDatabase(context)
    val dao = database.eventDao()
    val apiService = EventApiConfig.getApiService()

    return EventRepository.getInstance(apiService, dao)
  }

  fun provideSettingPreferences(context: Context): SettingPreferences {
    return SettingPreferences.getInstance(context.dataStore)
  }
}
