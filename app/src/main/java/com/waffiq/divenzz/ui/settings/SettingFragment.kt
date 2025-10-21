package com.waffiq.divenzz.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.waffiq.divenzz.R.color.md_theme_secondaryContainer
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.core.data.datastore.dataStore
import com.waffiq.divenzz.databinding.FragmentSettingBinding
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.isDarkTheme
import com.waffiq.divenzz.utils.ThemeMode
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

  private var _binding: FragmentSettingBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: SettingViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentSettingBinding.inflate(inflater, container, false)

    val pref = SettingPreferences.getInstance(requireActivity().dataStore)
    val factory = ViewModelFactory.getInstance(requireActivity().application, pref)
    viewModel = ViewModelProvider(requireActivity(), factory)[SettingViewModel::class.java]

    return binding.root
  }

  @OptIn(FlowPreview::class)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      viewModel.themeSettings.debounce(250L).collect {
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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}