package com.example.remindmeahead.database

import androidx.room.TypeConverter
import java.sql.Date


class converters {
    @TypeConverter
    fun DateToLong(date: Date):Long{
        return date.time
    }
    @TypeConverter
    fun LongToDate(timeLong:Long):Date{
        return Date(timeLong)
    }
}