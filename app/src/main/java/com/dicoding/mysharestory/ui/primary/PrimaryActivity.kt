package com.dicoding.mysharestory.ui.primary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.mysharestory.databinding.ActivityPrimaryBinding

class PrimaryActivity : AppCompatActivity() {

    private var _binding: ActivityPrimaryBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPrimaryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}