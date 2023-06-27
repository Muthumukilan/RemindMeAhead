package com.example.remindmeahead.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "eventTable")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eid:Int,
    val category:String,
    val fname:String,
    val lname:String,
    val date: Date,
    val toRemind:Date,
    val fKey:Int=0,
)
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val nid: Int,
    val notesToSend:String="",
    val number:String="",
    val sent:Boolean=false
)
