package com.tchibo.plantbuddy.repo

import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDao
import kotlinx.coroutines.flow.Flow

class OfflineRaspberryRepo(
    private val raspberryInfoDao: RaspberryInfoDao
): GenericRepo<RaspberryInfo> {
    fun getAllItemsStream(): Flow<List<RaspberryInfo>> =
        raspberryInfoDao.getAll()

    fun getItemStream(id: String): Flow<RaspberryInfo?> =
        raspberryInfoDao.getById(id)

    override suspend fun insertItem(item: RaspberryInfo) =
        raspberryInfoDao.insert(item)

    override suspend fun deleteItem(item: RaspberryInfo) =
        raspberryInfoDao.delete(item)

    override suspend fun updateItem(item: RaspberryInfo) =
        raspberryInfoDao.update(item)

    override suspend fun clear() =
        raspberryInfoDao.clear()
}