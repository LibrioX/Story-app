package com.example.dicodingstoryapp.fragment.showStory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.data.api.model.Story
import com.example.dicodingstoryapp.databinding.FragmentShowStoryBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactory
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.preferences.UserPreferences
import com.example.dicodingstoryapp.file.loadImage


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class ShowStoryFragment : Fragment() {

    private var _binding: FragmentShowStoryBinding? = null
    private val binding get() = _binding!!

    private val showStoryViewModel by viewModels<ShowStoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMode(ShowStoryFragmentArgs.fromBundle(requireArguments()).id)

        binding.myToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun showMode(id: String) {
        val pref = UserPreferences.getInstance(requireContext().dataStore)

        val tokenViewModel =
            ViewModelProvider(this, PreferencesViewModelFactory(pref))[TokenViewModel::class.java]


        tokenViewModel.getToken().observe(viewLifecycleOwner) {
            showStoryViewModel.apply {
                setId(id)
                setToken(it)
                getDetailStories()
            }
        }

        showStoryViewModel.storyDetailResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    it.data.story?.let { story -> setStoryData(story) }
                }

                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }

                else -> {}
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setStoryData(it: Story) {
        binding.apply {
            tvDetailName.text = it.name
            tvDetailDescription.text = it.description
            txtDdate.text = it.createdAt
            ivDetailPhoto.loadImage(it.photoUrl)
        }
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}