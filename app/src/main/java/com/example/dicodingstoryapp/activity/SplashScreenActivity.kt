package com.example.dicodingstoryapp.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.databinding.ActivitySplashScreenBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.preferences.UserPreferences
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = UserPreferences.getInstance(dataStore)

        val tokenViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[TokenViewModel::class.java]

        tokenViewModel.getLogin().observe(this) {
            if (it) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

    }
}