package com.tchibo.plantbuddy.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RaspberryInfoDao {

    @Query("SELECT * FROM raspberryinfo WHERE raspberryId = :id")
    fun getById(id: String): Flow<RaspberryInfo?>

    @Query("SELECT * FROM raspberryinfo")
    fun getAll(): Flow<List<RaspberryInfo>>

    @Query("SELECT raspberryId, raspberryName, raspberryStatus FROM raspberryinfo")
    fun getAllDto(): Flow<List<RaspberryInfoDto>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(raspberryInfo: RaspberryInfo)

    @Update
    suspend fun update(raspberryInfo: RaspberryInfo)

    @Delete
    suspend fun delete(raspberryInfo: RaspberryInfo)

    @Query("DELETE FROM raspberryinfo")
    suspend fun clear()
}