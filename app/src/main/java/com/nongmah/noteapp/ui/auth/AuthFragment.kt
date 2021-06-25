package com.nongmah.noteapp.ui.auth

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nongmah.noteapp.R
import com.nongmah.noteapp.data.remote.BasicAuthInterceptor
import com.nongmah.noteapp.databinding.FragmentAuthBinding
import com.nongmah.noteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.nongmah.noteapp.other.Constants.KEY_PASSWORD
import com.nongmah.noteapp.other.Status
import com.nongmah.noteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private lateinit var binding: FragmentAuthBinding

    private var currentEmail: String? = null
    private var currentPassword: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        binding = FragmentAuthBinding.bind(view)

        subscribeToObservers()

        binding.apply {
            btnRegister.setOnClickListener {
                val email = binding.etRegisterEmail.text.toString()
                val password = binding.etRegisterPassword.text.toString()
                val confirmedPassword = binding.etRegisterPasswordConfirm.text.toString()
                viewModel.register(email, password, confirmedPassword)
            }
            btnLogin.setOnClickListener {
                val email = binding.etLoginEmail.text.toString()
                val password = binding.etLoginPassword.text.toString()
                currentEmail = email
                currentPassword = password
                viewModel.login(email, password)
            }
        }
    }

    private fun authenticateApi(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
            navOptions
        )
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully logged in")
                        sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL, currentEmail).apply()
                        sharedPref.edit().putString(KEY_PASSWORD, currentPassword).apply()
                        authenticateApi(currentEmail ?: "", currentPassword ?: "")
                        redirectLogin()
                    }
                    Status.ERROR -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        binding.registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status) {
                    Status.SUCCESS -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully registered an account")
                    }
                    Status.ERROR -> {
                        binding.registerProgressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        binding.registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}