package com.nongmah.noteapp.ui.notes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nongmah.noteapp.R
import com.nongmah.noteapp.data.remote.BasicAuthInterceptor
import com.nongmah.noteapp.databinding.FragmentNotesBinding
import com.nongmah.noteapp.other.Constants
import com.nongmah.noteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

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

        val binding = FragmentNotesBinding.bind(view)

        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(""))
        }
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
        basicAuthInterceptor.email = null
        basicAuthInterceptor.password = null

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.notesFragment, true).build()
        findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAuthFragment(), navOptions)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }
}