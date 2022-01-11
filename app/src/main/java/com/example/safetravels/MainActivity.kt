package com.example.safetravels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.safetravels.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

//        supportActionBar?.hide()
        setSupportActionBar(binding.mainToolbar)

        setContentView(binding.root)
    }
}