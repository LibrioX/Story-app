package com.example.dicodingstoryapp.fragment.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.databinding.FragmentLoginBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactory
import com.example.dicodingstoryapp.preferences.UserPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        val pref = UserPreferences.getInstance(requireContext().dataStore)

        val tokenViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[TokenViewModel::class.java]


        binding.btnToRegister.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        binding.btnLogin.setOnClickListener {
            binding.apply {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                val valid = emailInputLayout.error == null && passwordInputLayout.error == null
                        && email.isNotEmpty() && password.isNotEmpty()

                if (valid) loginViewModel.loginUser(email, password)
                else toastMaker(resources.getString(R.string.errorHandlingLogin))
            }

        }

        tokenViewModel.getLogin().observe(viewLifecycleOwner) {
            if (it) moveToHome()
        }


        loginViewModel.loginResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)

                    it.data.loginResult?.token?.let { token ->
                        tokenViewModel.saveToken("Bearer $token")
                        tokenViewModel.saveLogin(true)
                    }
                    toastMaker(resources.getString(R.string.loginSuccess))

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


    private fun moveToHome() {
        view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeActivity)
        requireActivity().finish()
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {

        val textView = ObjectAnimator.ofFloat(binding.titleText, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.emailInputLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordInputLayout, View.ALPHA, 1f).setDuration(500)
        val forgotPassword =
            ObjectAnimator.ofFloat(binding.buttonForgotPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(textView, email, password, forgotPassword, btnLogin, btnRegister)
            startDelay = 500
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}