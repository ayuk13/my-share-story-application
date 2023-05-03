package com.dicoding.mysharestory.ui.story

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mysharestory.databinding.ActivityStoryBinding
import com.dicoding.mysharestory.utils.ViewModelFactory

class StoryActivity : AppCompatActivity() {

    private val viewModel: ListStoryViewModel by viewModels()
    { ViewModelFactory(this) }
    private var _binding: ActivityStoryBinding? = null
    private val binding get() = _binding
    private var tokenUser: String = ""
    private var nameUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.getTokenUser().observe(this) { token ->
            token?.let {
                tokenUser = token
            }
        }

        viewModel.getNameUser().observe(this) { name ->
            name?.let {
                nameUser = name
            }
        }
    }
}