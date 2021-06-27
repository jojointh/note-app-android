package com.nongmah.noteapp.ui.addeditnote

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.nongmah.noteapp.R
import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.databinding.FragmentAddEditNoteBinding
import com.nongmah.noteapp.other.Constants.DEFAULT_NOTE_COLOR
import com.nongmah.noteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.nongmah.noteapp.other.Constants.NO_EMAIL
import com.nongmah.noteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note) {

    private val viewModel: AddEditNoteViewModel by viewModels()

    private val args: AddEditNoteFragmentArgs by navArgs()

    lateinit var binding: FragmentAddEditNoteBinding

    private var currentNote: Note? = null
    private var currentNoteColor = DEFAULT_NOTE_COLOR

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditNoteBinding.bind(view)
        if (args.id.isNotEmpty()) {
            viewModel.getNoteById(args.id)
            subscribeToObservers()
        }
    }

    private fun subscribeToObservers() {

    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote() {
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL

        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()
        if (title.isEmpty() || content.isEmpty()) {
            return
        }
        val date = System.currentTimeMillis()
        val color = currentNoteColor
        val id = currentNote?.id ?: UUID.randomUUID().toString()
        val owners = currentNote?.owners ?: listOf(authEmail)
        val note = Note(title, content, date, owners, color, id = id)
        viewModel.insertNote(note)
    }
}