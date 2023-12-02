package com.tchibo.plantbuddy.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RaspberryInfoDao {

    @Query("SELECT * FROM raspberryinfo")
    fun getAll(): List<RaspberryInfo>

    @Query("SELECT raspberryId, raspberryName, raspberryStatus FROM raspberryinfo")
    fun getAllDto(): List<RaspberryInfoDto>

    @Insert
    fun insertAll(vararg raspberryInfos: List<RaspberryInfo>)

    @Delete
    fun delete(raspberryInfo: RaspberryInfo)
}