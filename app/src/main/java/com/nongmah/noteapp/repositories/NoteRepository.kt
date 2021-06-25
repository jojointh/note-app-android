package com.nongmah.noteapp.repositories

import android.app.Application
import com.nongmah.noteapp.data.local.NoteDao
import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.data.remote.NoteApi
import com.nongmah.noteapp.data.remote.requests.AccountRequest
import com.nongmah.noteapp.other.Resource
import com.nongmah.noteapp.other.checkForInternetConnection
import com.nongmah.noteapp.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                noteApi.getNote()
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    // TODO: insert notes in database
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