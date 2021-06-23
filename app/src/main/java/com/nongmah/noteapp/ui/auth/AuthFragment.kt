package com.nongmah.noteapp.ui.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nongmah.noteapp.R
import com.nongmah.noteapp.databinding.FragmentAuthBinding
import com.nongmah.noteapp.ui.BaseFragment

class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAuthBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNotesFragment())
        }
    }
}