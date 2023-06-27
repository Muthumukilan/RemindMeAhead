package com.example.remindmeahead.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


interface viewAbstract{
    val allData:Flow<List<Event>>
    fun allCat(category: String):Flow<List<Event>>
    fun getNote(fid:Int):Note
    fun addEvent(event: Event)
    fun addNote(note: Note)
    fun deleteEvent(event: Event)
    fun deleteNote(note: Note)
    fun updateEvent(event: Event)
    fun updateNote(note: Note)
}

@HiltViewModel
class MainViewModel
@Inject constructor(private val repo: repo):ViewModel(),viewAbstract
{
    private val ioScope= CoroutineScope(Dispatchers.IO)
    override val allData: Flow<List<Event>> = repo.getAll()
    override fun allCat(category: String): Flow<List<Event>> = repo.getByCat(category)
    override fun getNote(fid:Int): Note= repo.getNote(fid)

    override fun addEvent(event: Event) {
        ioScope.launch { repo.insertEvent(event = event) }
    }

    override fun addNote(note: Note) {
        ioScope.launch { repo.insertNote(note) }
    }

    override fun deleteEvent(event: Event) {
        ioScope.launch { repo.deletEvent(event) }
    }

    override fun deleteNote(note: Note) {
        ioScope.launch { repo.deleteNote(note) }
    }

    override fun updateEvent(event: Event) {
        ioScope.launch { repo.updateEvent(event) }
    }

    override fun updateNote(note: Note) {
        ioScope.launch { repo.updateNote(note) }
    }

}