package com.example.remindmeahead.database

import kotlinx.coroutines.flow.Flow

class repo(private val dbDao: dbDao) {
    suspend fun insertEvent(event: Event)=dbDao.insertEvent(event = event)
    suspend fun insertNote(note: Note)=dbDao.insertNote(note = note)
    suspend fun deletEvent(event: Event)=dbDao.deletEvent(event = event)
    suspend fun deleteNote(note: Note)=dbDao.deleteNote(note = note)
    suspend fun updateEvent(event: Event)=dbDao.updateEvent(event = event)
    suspend fun updateNote(note: Note)=dbDao.updateNote(note = note)
    fun getAll(): Flow<List<Event>> = dbDao.getAll()
    fun getById(eid:Int): Flow<List<Event>> = dbDao.getById(eid = eid)
    fun getByCat(category:String): Flow<List<Event>> = dbDao.getByCat(category = category)
    fun getNote(fid:Int): Note= dbDao.getNote(fid = fid)
}