package com.tchibo.plantbuddy.repo

import com.tchibo.plantbuddy.domain.RaspberryInfo
import com.tchibo.plantbuddy.domain.RaspberryInfoDao
import kotlinx.coroutines.flow.Flow

class OfflineRaspberryInfoRepo(
    private val raspberryInfoDao: RaspberryInfoDao
): RaspberryInfoRepo {
    override fun getAllItemsStream(): Flow<List<RaspberryInfo>> =
        raspberryInfoDao.getAll()

    override fun getItemStream(id: Int): Flow<RaspberryInfo?> =
        raspberryInfoDao.getById(id)

    override suspend fun insertItem(item: RaspberryInfo) =
        raspberryInfoDao.insert(item)

    override suspend fun deleteItem(item: RaspberryInfo) =
        raspberryInfoDao.delete(item)

    override suspend fun updateItem(item: RaspberryInfo) =
        raspberryInfoDao.update(item)
}