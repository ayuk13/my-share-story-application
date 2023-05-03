package com.dicoding.mysharestory.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.dicoding.mysharestory.databinding.ActivityMainBinding
import com.dicoding.mysharestory.ui.primary.PrimaryActivity
import com.dicoding.mysharestory.ui.story.StoryActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        runBlocking {
            lifecycleScope.launch {
                delay(1000)
                observeLiveData()
            }
        }
    }

    private fun observeLiveData() {
        viewModel.getTokenUser().observe(this) { token ->
            Log.d("TAG", "observeLiveData: $token")
            token?.let {
                if (token.isNotEmpty()) {
                    Intent(this@MainActivity, StoryActivity::class.java).apply {
                        startActivity(this)
                    }.also { finish() }
                } else {
                    Intent(this@MainActivity, PrimaryActivity::class.java).apply {
                        startActivity(this)
                    }.also { finish() }
                }
            }
        }
    }
}