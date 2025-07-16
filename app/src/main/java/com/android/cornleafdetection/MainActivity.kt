package com.android.cornleafdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.cornleafdetection.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
                ?: throw IllegalStateException("NavController not found")

        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, false)
            } else {
                navController.popBackStack(R.id.navigation_home, false)
                navController.navigate(item.itemId)
            }
            true
        }

        binding.btnCamera.setOnClickListener {
            navView.selectedItemId = R.id.navigation_detection
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
                ?: return false
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
