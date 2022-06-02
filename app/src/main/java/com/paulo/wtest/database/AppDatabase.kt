package com.paulo.wtest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paulo.wtest.database.dao.postalcode.PostalCodeDao
import com.paulo.wtest.model.postalcode.PostalCode

@Database(
    entities = [
        PostalCode::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    companion object{
        const val NAME = "codigos_postais.db"
    }
    abstract fun postalCodeDao(): PostalCodeDao
}