package com.nongmah.noteapp.ui.notedetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nongmah.noteapp.R
import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.databinding.FragmentNoteDetailBinding
import com.nongmah.noteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    lateinit var binding: FragmentNoteDetailBinding

    private var currentNote: Note? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteDetailBinding.bind(view)

        subscribeToObservers()

        binding.fabEditNote.setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id)
            )
        }
    }

    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(binding.tvNoteContent, markdown)
    }

    private fun subscribeToObservers() {
        viewModel.observeNoteByID(args.id).observe(viewLifecycleOwner, Observer {
            it?.let { note ->
                binding.tvNoteTitle.text = note.title
                setMarkdownText(note.content)
                currentNote = note
            } ?: showSnackbar("Note not found")
        })
    }
}