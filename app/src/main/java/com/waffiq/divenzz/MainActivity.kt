package com.waffiq.divenzz

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.waffiq.divenzz.R.id.nav_host_fragment_activity_main
import com.waffiq.divenzz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT ),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT )
    )
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupNavigation()
  }

  private fun setupNavigation(){
    val navHostFragment =
      supportFragmentManager.findFragmentById(nav_host_fragment_activity_main) as NavHostFragment

    binding.navView.setupWithNavController(navHostFragment.navController)
  }
}
