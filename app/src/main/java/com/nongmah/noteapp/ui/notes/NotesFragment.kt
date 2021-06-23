package com.nongmah.noteapp.ui.notes

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nongmah.noteapp.R
import com.nongmah.noteapp.databinding.FragmentNotesBinding
import com.nongmah.noteapp.ui.BaseFragment

class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNotesBinding.bind(view)

        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(""))
        }

    }
}