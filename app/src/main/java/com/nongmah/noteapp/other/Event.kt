package com.nongmah.noteapp.other

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

    fun peekContent(): T = content
}