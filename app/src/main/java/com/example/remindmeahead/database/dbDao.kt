package com.example.remindmeahead.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface dbDao {
    //inserts
    @Upsert
    suspend fun insertEvent(event: Event)
    @Upsert
    suspend fun insertNote(note: Note)
    //Delete
    @Delete
    suspend fun deletEvent(event: Event)
    @Delete
    suspend fun deleteNote(note: Note)
    //update
    @Update
    suspend fun updateEvent(event: Event)
    @Update
    suspend fun updateNote(note: Note)
    //Query
    @Query("select * from eventTable")
    fun getAll():Flow<List<Event>>
    @Query("select * from eventTable where eid=:eid")
    fun getById(eid:Int):Flow<List<Event>>
    @Query("select * from eventTable where category=:category")
    fun getByCat(category:String):Flow<List<Event>>
    @Query("select * from note where nid=:fid")
    fun getNote(fid:Int):Note
}