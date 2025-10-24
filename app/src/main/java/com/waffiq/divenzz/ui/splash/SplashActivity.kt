package com.waffiq.divenzz.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.waffiq.divenzz.MainActivity
import com.waffiq.divenzz.ui.settings.SettingViewModel
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

  private lateinit var splashScreen: SplashScreen
  private lateinit var viewModel: SettingViewModel
  private var keepSplash = true

  override fun onCreate(savedInstanceState: Bundle?) {
    splashScreen = installSplashScreen()
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    splashScreen.setKeepOnScreenCondition { keepSplash }

    val factory = ViewModelFactory.getInstance(this)
    viewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

    lifecycleScope.launch {
      when (viewModel.themeSettings.first()) {
        ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ThemeMode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
      }
    }

    startActivity(Intent(this, MainActivity::class.java))
    finishAffinity()
  }
}