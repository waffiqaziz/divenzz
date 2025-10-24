package com.waffiq.divenzz.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.utils.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

  val themeSettings: Flow<ThemeMode> = pref.getThemeSetting().distinctUntilChanged()

  fun saveThemeSetting(themeMode: ThemeMode) {
    viewModelScope.launch {
      pref.saveThemeSetting(themeMode)
    }
  }
}
