package com.nongmah.noteapp.ui.notes

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nongmah.noteapp.R
import com.nongmah.noteapp.adapters.NoteAdapter
import com.nongmah.noteapp.databinding.FragmentNotesBinding
import com.nongmah.noteapp.other.Constants
import com.nongmah.noteapp.other.Status
import com.nongmah.noteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private val viewModel: NotesViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var binding: FragmentNotesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER
        binding = FragmentNotesBinding.bind(view)

        setupRecyclerView()
        subscribeToObservers()

        noteAdapter.setOnItemClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(it.id))
        }

        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment("")
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        noteAdapter.notes = result.data!!
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() = binding.rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miLogout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        sharedPreferences.edit().putString(Constants.KEY_LOGGED_IN_EMAIL, Constants.NO_EMAIL).apply()
        sharedPreferences.edit().putString(Constants.KEY_PASSWORD, Constants.NO_PASSWORD).apply()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.notesFragment, true).build()
        findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAuthFragment(), navOptions)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }
}