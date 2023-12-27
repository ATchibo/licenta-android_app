package com.tchibo.plantbuddy.repo

import android.util.Log
import com.tchibo.plantbuddy.domain.MoistureInfo
import com.tchibo.plantbuddy.domain.MoistureInfoDao
import kotlinx.coroutines.flow.Flow

class MoistureInfoRepo (
    private val moistureInfoDao: MoistureInfoDao
): GenericRepo<MoistureInfo> {

//    fun getItemsWithRaspIdStream(id: String): Flow<MoistureInfo?> =
//        moistureInfoDao.getByRaspberryId(id)

    fun getItemsWithRaspIdStream(id: String): Flow<MoistureInfo?> {
        Log.d("MoistureInfoRepo", "Fetching data for id: $id")
        return moistureInfoDao.getByRaspberryId(id)
    }

    override suspend fun insertItem(item: MoistureInfo) =
        moistureInfoDao.insert(item)

    override suspend fun deleteItem(item: MoistureInfo) =
        moistureInfoDao.delete(item)

    override suspend fun updateItem(item: MoistureInfo) =
        moistureInfoDao.update(item)

    override suspend fun clear() =
        moistureInfoDao.clear()
}