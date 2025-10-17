package com.waffiq.divenzz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.waffiq.divenzz.R.id.nav_host_fragment_activity_main
import com.waffiq.divenzz.R.id.navigation_search
import com.waffiq.divenzz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupNavigation()
  }

  private fun setupNavigation() {
    val navHostFragment =
      supportFragmentManager.findFragmentById(nav_host_fragment_activity_main) as NavHostFragment

    binding.navView.setupWithNavController(navHostFragment.navController)

    binding.navView.setOnItemReselectedListener { menuItem ->
      if (menuItem.itemId == navigation_search) {
        supportFragmentManager.setFragmentResult("open_search_view", Bundle.EMPTY)
      }
    }
  }
}
