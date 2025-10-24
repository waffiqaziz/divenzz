package com.waffiq.divenzz.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.waffiq.divenzz.utils.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

  fun getThemeSetting(): Flow<ThemeMode> {
    return dataStore.data.map { preferences ->
      val mode = preferences[THEME_KEY]
      ThemeMode.valueOf(mode ?: ThemeMode.SYSTEM.name)
    }
  }

  suspend fun saveThemeSetting(mode: ThemeMode) {
    dataStore.edit { preferences ->
      preferences[THEME_KEY] = mode.name
    }
  }

  companion object{
    val THEME_KEY = stringPreferencesKey("theme_mode")

    @Volatile
    private var INSTANCE: SettingPreferences? = null

    fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
      return INSTANCE ?: synchronized(this){
        val instance = SettingPreferences(dataStore)
        INSTANCE = instance
        instance
      }
    }
  }
}
