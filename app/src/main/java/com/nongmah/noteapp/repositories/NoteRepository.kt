package com.nongmah.noteapp.repositories

import android.app.Application
import com.nongmah.noteapp.data.local.NoteDao
import com.nongmah.noteapp.data.local.entities.LocallyDeletedNoteID
import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.data.remote.NoteApi
import com.nongmah.noteapp.data.remote.requests.AccountRequest
import com.nongmah.noteapp.data.remote.requests.DeleteNoteRequest
import com.nongmah.noteapp.other.Resource
import com.nongmah.noteapp.other.checkForInternetConnection
import com.nongmah.noteapp.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.addNote(note)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply {
                isSynced = true
            })
        } else {
            noteDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    suspend fun deleteNote(noteID: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteID))
        } catch (e: Exception) {
            null
        }
        noteDao.deleteNoteById(noteID)
        if (response == null || !response.isSuccessful) {
            noteDao.insertLocallyDeletedNoteID(LocallyDeletedNoteID(noteID))
        } else {
            deleteLocallyDeletedNoteID(noteID)
        }
    }

    fun observeNoteByID(noteID: String) = noteDao.observeNoteById(noteID)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteID: String) {
        noteDao.deleteLocallyDeletedNoteID(deletedNoteID)
    }

    suspend fun getNoteById(noteID: String) = noteDao.getNoteById(noteID)

    private suspend fun syncNotes(): Response<List<Note>> {
        val locallyDeletedNoteIDs = noteDao.getAllLocallyDeletedNoteIDs()
        locallyDeletedNoteIDs.forEach { id -> deleteNote(id.deletedNoteID) }

        val unsyncedNotes = noteDao.getAllUnsyncedNotes()
        unsyncedNotes.forEach { note -> insertNote(note) }

        return noteApi.getNotes()
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                syncNotes()
            },
            saveFetchResult = { response ->
                response.body()?.let { notes ->
                    noteDao.deleteAllNotes()
                    insertNotes(notes.onEach { note -> note.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequest(email, password))
            if (response.isSuccessful && response.body()?.successful == true) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the server. Check your internet connection", null)
        }
    }

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
            if (response.isSuccessful && response.body()?.successful == true) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the server. Check your internet connection", null)
        }
    }
}