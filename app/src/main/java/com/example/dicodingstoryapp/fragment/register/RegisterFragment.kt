package com.example.dicodingstoryapp.fragment.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.databinding.FragmentRegisterBinding
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactory


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnToLogin.setOnClickListener {
            onBackPress()
        }

        binding.btnRegister.setOnClickListener {
            binding.apply {
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()
                val username = edRegisterName.text.toString()
                val valid =
                    emailInputLayoutEmail.error == null && passwordInputLayoutPassword.error == null
                            && email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()

                if (valid) registerViewModel.registerUser(username, email, password)
                else toastMaker(resources.getString(R.string.errorHandlingRegister))
            }

        }

        registerViewModel.registerResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    dialogMaker()
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
                else -> {}
            }
        }

        playAnimation()

    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun dialogMaker() {
        AwesomeDialog.build(requireActivity())
            .title(resources.getString(R.string.greeting))
            .body(resources.getString(R.string.greetingBody))
            .onPositive(resources.getString(R.string.join_login)) {
                onBackPress()
            }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {

        val textView = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val username =
            ObjectAnimator.ofFloat(binding.textInputLayoutUsername, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.emailInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordInputLayoutPassword, View.ALPHA, 1f)
            .setDuration(500)
        val btnToLogin = ObjectAnimator.ofFloat(binding.btnToLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(textView, username, email, password, btnToLogin, btnRegister)
            startDelay = 500
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}