package com.nongmah.noteapp.data.remote

import com.nongmah.noteapp.data.local.entities.Note
import com.nongmah.noteapp.data.remote.requests.AccountRequest
import com.nongmah.noteapp.data.remote.requests.AddOwnerRequest
import com.nongmah.noteapp.data.remote.requests.DeleteNoteRequest
import com.nongmah.noteapp.data.remote.responses.SimpleResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NoteApi {

    @POST("/register")
    suspend fun register(
        @Body registerRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/login")
    suspend fun login(
        @Body loginRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/add-note")
    suspend fun addNote(
        @Body note: Note
    ): Response<ResponseBody>

    @POST("/delete-note")
    suspend fun deleteNote(
        @Body deleteNoteRequest: DeleteNoteRequest
    ): Response<ResponseBody>

    @POST("/add-owner-to-note")
    suspend fun addOwnerToNote(
        @Body addOwnerRequest: AddOwnerRequest
    ): Response<SimpleResponse>

    @GET("/get-notes")
    suspend fun getNote(): Response<List<Note>>
}