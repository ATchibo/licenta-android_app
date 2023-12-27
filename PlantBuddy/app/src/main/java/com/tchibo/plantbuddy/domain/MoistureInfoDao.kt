package com.tchibo.plantbuddy.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoistureInfoDao {
    @Query("SELECT * FROM moistureinfo WHERE raspberryId = :id")
    fun getByRaspberryId(id: String): Flow<MoistureInfo?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(moistureInfo: MoistureInfo)

    @Update
    suspend fun update(moistureInfo: MoistureInfo)

    @Delete
    suspend fun delete(moistureInfo: MoistureInfo)

    @Query("DELETE FROM moistureinfo")
    suspend fun clear()
}