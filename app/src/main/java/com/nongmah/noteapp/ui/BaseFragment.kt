package com.nongmah.noteapp.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutId: Int): Fragment(layoutId) {

    fun showSnackbar(text: String) {
        try {
            (requireActivity() as SnackBarListener).showSnackBar(text)
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException("Activity must implement ${SnackBarListener::class.simpleName}")
        }
    }
}