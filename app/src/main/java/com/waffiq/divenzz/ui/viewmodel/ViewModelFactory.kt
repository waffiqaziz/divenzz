package com.waffiq.divenzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.ui.settings.SettingViewModel

class ViewModelFactory(private val pref: SettingPreferences) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
      return SettingViewModel(pref) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
  }
}