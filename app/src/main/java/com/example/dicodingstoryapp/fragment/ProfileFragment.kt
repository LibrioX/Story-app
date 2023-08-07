package com.example.dicodingstoryapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.activity.HomeActivity
import com.example.dicodingstoryapp.activity.MainActivity
import com.example.dicodingstoryapp.databinding.FragmentProfileBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.preferences.UserPreferences


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        val tokenViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[TokenViewModel::class.java]

        binding.actionLogout.setOnClickListener {
            tokenViewModel.clearPreferences()
            moveToLogin()
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun moveToLogin() {
        val activity = requireActivity() as HomeActivity
        startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}