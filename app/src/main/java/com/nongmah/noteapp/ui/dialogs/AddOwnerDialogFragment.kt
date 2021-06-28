package com.nongmah.noteapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nongmah.noteapp.R
import com.nongmah.noteapp.databinding.EditTextEmailBinding

class AddOwnerDialogFragment : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = EditTextEmailBinding.inflate(LayoutInflater.from(requireContext()))

        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_add_person)
            .setTitle("Add Owner to Note")
            .setMessage("Enter an E-Mail of a person you want to share the note with." +
                "This person will be able to read and edit the note.")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                val email = binding.etAddOwnerEmail.text.toString()
                positiveListener?.let { yes ->
                    yes(email)
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }
}