package com.paulo.wtest.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    // OnConflictStrategy.REPLACE used in: CreateNoteRepository
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(obj: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(obj: List<T>)

    // Insert an array of objects in the database.
    @Insert
    fun insert(vararg obj: T)

    @Update
    fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)
}