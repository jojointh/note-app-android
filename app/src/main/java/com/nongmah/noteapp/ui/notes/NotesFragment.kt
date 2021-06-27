package com.nongmah.noteapp.ui.notes

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

    private val swipingItem = MutableLiveData(false)

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
        setupSwipeRefreshLayout()
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
        swipingItem.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isEnabled = !it
        })
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                Log.d("aaa", "start swipe " + isCurrentlyActive)
                swipingItem.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            viewModel.deleteNote(note.id)
            Snackbar.make(requireView(), "Note was successfully deleted", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertNote(note)
                    viewModel.deleteLocallyDeletedNoteID(note.id)
                }
                show()
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllNotes()
        }
    }

    private fun setupRecyclerView() = binding.rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
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