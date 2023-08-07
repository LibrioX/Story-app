package com.example.dicodingstoryapp.fragment.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.adapter.LoadingStateAdapter
import com.example.dicodingstoryapp.adapter.StoryAdapter
import com.example.dicodingstoryapp.databinding.FragmentHomeBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.preferences.UserPreferences
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactoryWithToken


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var homeViewModel: HomeViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        val tokenViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[TokenViewModel::class.java]

        tokenViewModel.getToken().observe(viewLifecycleOwner) {

            homeViewModel = ViewModelProvider(
                this,
                ViewModelFactoryWithToken.getInstance(requireActivity(), it)
            )[HomeViewModel::class.java]

            setUsersData()

        }

        binding.btnAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }

    }

    private fun setUsersData() {
        val adapter = StoryAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        homeViewModel?.stories?.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}