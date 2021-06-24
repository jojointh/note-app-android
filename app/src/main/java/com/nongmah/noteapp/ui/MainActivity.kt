package com.nongmah.noteapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nongmah.noteapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

interface SnackBarListener {
    fun showSnackBar(text: String)
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SnackBarListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showSnackBar(text: String) {
        Snackbar.make(
            binding.rootLayout,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}