package com.tagai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tagai.data.local.dao.NoteDao
import com.tagai.data.local.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 3, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}