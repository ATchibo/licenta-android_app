package com.tchibo.plantbuddy.repo

import com.tchibo.plantbuddy.domain.RaspberryInfo
import kotlinx.coroutines.flow.Flow

interface RaspberryInfoRepo {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<RaspberryInfo>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<RaspberryInfo?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: RaspberryInfo)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: RaspberryInfo)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: RaspberryInfo)

}