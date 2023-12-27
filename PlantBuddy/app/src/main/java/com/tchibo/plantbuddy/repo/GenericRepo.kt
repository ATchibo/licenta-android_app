package com.tchibo.plantbuddy.repo

interface GenericRepo<T> {
//    /**
//     * Retrieve all the items from the the given data source.
//     */
//    fun getAllItemsStream(): Flow<List<T>>
//
//    /**
//     * Retrieve an item from the given data source that matches with the [id].
//     */
//    fun getItemStream(id: String): Flow<T?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: T)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: T)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: T)

    /**
     * Clear all the items from the data source
     */
    suspend fun clear()
}