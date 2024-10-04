package com.example.test

import androidx.room.*
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>
    @Insert
    suspend fun insert(note: Note)
    @Delete
    suspend fun delete(note: Note)
    @Query("UPDATE notes SET title = :title, content = :content WHERE id = :id")
    suspend fun update(id: Int, title: String, content: String)
}

