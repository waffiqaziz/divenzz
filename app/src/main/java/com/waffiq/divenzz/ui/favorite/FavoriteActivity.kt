package com.waffiq.divenzz.ui.favorite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffiq.divenzz.core.data.datastore.SettingPreferences
import com.waffiq.divenzz.core.data.datastore.dataStore
import com.waffiq.divenzz.databinding.ActivityFavoriteBinding
import com.waffiq.divenzz.ui.adapter.FavoriteAdapter
import com.waffiq.divenzz.ui.viewmodel.ViewModelFactory
import com.waffiq.divenzz.utils.Helpers.openDetailPage
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

  private lateinit var binding: ActivityFavoriteBinding

  private lateinit var viewModel: FavoriteViewModel
  private lateinit var favoriteAdapter: FavoriteAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    binding = ActivityFavoriteBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val pref = SettingPreferences.getInstance(this.dataStore)
    viewModel = ViewModelProvider(
      this,
      ViewModelFactory(this.application, pref)
    )[FavoriteViewModel::class.java]

    setupRecyclerView()
    getFavorites()
  }

  private fun setupRecyclerView() {
    favoriteAdapter = FavoriteAdapter(::onClick)
    binding.rvEvents.layoutManager =
      LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    binding.rvEvents.adapter = favoriteAdapter
  }

  private fun onClick(id: Int) {
    openDetailPage(id)
  }

  private fun getFavorites() {
    // observe favorite events
    lifecycleScope.launch {
      viewModel.getAllEvents.collect { favorites ->
        if (favorites.isNotEmpty()) {
          binding.emptyState.root.isVisible = false
          binding.rvEvents.isVisible = true
          favoriteAdapter.setFavorite(favorites)
        } else {
          binding.emptyState.root.isVisible = true
          binding.rvEvents.isVisible = false
        }
      }
    }
  }
}
