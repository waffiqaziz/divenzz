package com.waffiq.divenzz.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.waffiq.divenzz.BuildConfig
import com.waffiq.divenzz.R.color.md_theme_secondaryContainer
import com.waffiq.divenzz.R.string.daily_notification_disabled
import com.waffiq.divenzz.R.string.daily_notification_enabled_at_7_00_am
import com.waffiq.divenzz.R.string.notification_permission_is_required
import com.waffiq.divenzz.R.string.one_time_test_scheduled
import com.waffiq.divenzz.R.string.periodic_test_started_every_15_min
import com.waffiq.divenzz.R.string.periodic_test_stopped
import com.waffiq.divenzz.databinding.FragmentSettingBinding
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.isDarkTheme
import com.waffiq.divenzz.utils.Helpers.showShortToast
import com.waffiq.divenzz.utils.ThemeMode
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

  private var _binding: FragmentSettingBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: SettingViewModel

  private var permissionCallback: ((Boolean) -> Unit)? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentSettingBinding.inflate(inflater, container, false)

    val factory = ViewModelFactory.getInstance(requireActivity())
    viewModel = ViewModelProvider(requireActivity(), factory)[SettingViewModel::class.java]

    return binding.root
  }

  @OptIn(FlowPreview::class)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // show debug controls only in debug build
    binding.tvDebugHeader.isVisible = BuildConfig.DEBUG
    binding.debugContainer.isVisible = BuildConfig.DEBUG

    lifecycleScope.launch {
      viewModel.themeSettings.debounce(350L).collect {
        when (it) {
          ThemeMode.SYSTEM -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            clearButtonHighlights()
            highlightActiveButton(ThemeMode.SYSTEM)
            binding.switchTheme.isChecked = requireActivity().isDarkTheme
          }

          ThemeMode.DARK -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            clearButtonHighlights()
            highlightActiveButton(ThemeMode.DARK)
            binding.switchTheme.isChecked = true
          }

          ThemeMode.LIGHT -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            clearButtonHighlights()
            highlightActiveButton(ThemeMode.LIGHT)
            binding.switchTheme.isChecked = false
          }
        }
      }
    }

    // load notification state
    lifecycleScope.launch {
      viewModel.isNotificationEnabled.collect { isEnabled ->
        binding.switchNotification.isChecked = isEnabled
      }
    }

    btnAction()
  }

  private fun btnAction() {
    binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        viewModel.saveThemeSetting(ThemeMode.DARK)
      } else {
        viewModel.saveThemeSetting(ThemeMode.LIGHT)
      }
    }

    binding.btnSystem.setOnClickListener {
      viewModel.saveThemeSetting(ThemeMode.SYSTEM)
    }

    binding.btnLight.setOnClickListener {
      viewModel.saveThemeSetting(ThemeMode.LIGHT)
    }

    binding.btnDark.setOnClickListener {
      viewModel.saveThemeSetting(ThemeMode.DARK)
    }

    binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
      if (binding.switchNotification.isPressed) {
        if (isChecked) {
          requestNotificationPermissionIfNeeded { granted ->
            if (granted) {
              // enable notification
              NotificationScheduler.scheduleDailyNotificationAlarm(requireContext())
              viewModel.setNotificationEnabled(true)
              requireActivity().showShortToast(getString(daily_notification_enabled_at_7_00_am))
            } else {
              binding.switchNotification.isChecked = false
            }
          }
        } else {
          // disable notification
          NotificationScheduler.cancelDailyNotificationAlarm(requireContext())
          viewModel.setNotificationEnabled(false)
          requireActivity().showShortToast(getString(daily_notification_disabled))
        }
      }
    }

    if (BuildConfig.DEBUG) {
      binding.switchNotificationDebug.setOnCheckedChangeListener { _, isChecked ->
        if (binding.switchNotificationDebug.isPressed) {
          if (isChecked) {
            requestNotificationPermissionIfNeeded { granted ->
              if (granted) {
                // enable notification
                NotificationScheduler.scheduleDailyNotificationWorker(requireContext())
                viewModel.setNotificationEnabled(true)
                requireActivity().showShortToast(getString(daily_notification_enabled_at_7_00_am))
              } else {
                binding.switchNotificationDebug.isChecked = false
              }
            }
          } else {
            // disable notification
            NotificationScheduler.cancelDailyNotificationWorker(requireContext())
            viewModel.setNotificationEnabled(false)
            requireActivity().showShortToast(getString(daily_notification_disabled))
          }
        }
      }

      // test once
      binding.btnTestOnce.setOnClickListener {
        requestNotificationPermissionIfNeeded { granted ->
          if (granted) {
            NotificationScheduler.scheduleOneTimeNotification(requireContext())
            requireActivity().showShortToast(getString(one_time_test_scheduled))
            Log.i(TAG, "One-time test triggered")
          }
        }
      }

      // test periodic
      binding.btnTestPeriodic.setOnClickListener {
        requestNotificationPermissionIfNeeded { granted ->
          if (granted) {
            NotificationScheduler.scheduleDebugPeriodicNotification(requireContext())
            requireContext().showShortToast(getString(periodic_test_started_every_15_min))
            Log.i(TAG, "Periodic test started")
          }
        }
      }

      binding.btnStopPeriodic.setOnClickListener {
        NotificationScheduler.cancelDebugPeriodicNotification(requireContext())
        requireActivity().showShortToast(getString(periodic_test_stopped))
        Log.i(TAG, "Periodic test stopped")
      }
    }
  }

  private fun highlightActiveButton(mode: ThemeMode) {
    val selectedColor = ActivityCompat.getColor(
      requireContext(),
      md_theme_secondaryContainer,
    )
    when (mode) {
      ThemeMode.SYSTEM -> binding.btnSystem.setBackgroundColor(selectedColor)
      ThemeMode.LIGHT -> binding.btnLight.setBackgroundColor(selectedColor)
      ThemeMode.DARK -> binding.btnDark.setBackgroundColor(selectedColor)
    }
  }

  private fun clearButtonHighlights() {
    val defaultColor = Color.TRANSPARENT // or your default button color
    binding.btnSystem.setBackgroundColor(defaultColor)
    binding.btnLight.setBackgroundColor(defaultColor)
    binding.btnDark.setBackgroundColor(defaultColor)
  }

  private fun requestNotificationPermissionIfNeeded(onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      when {
        ContextCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED -> {
          onResult(true)
        }

        else -> {
          permissionCallback = onResult
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    } else {
      // below Android 13, no permission needed
      onResult(true)
    }
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    permissionCallback?.invoke(isGranted)
    permissionCallback = null

    if (!isGranted) {
      requireActivity().showShortToast(getString(notification_permission_is_required))
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    const val TAG = "SettingFragment"
  }
}