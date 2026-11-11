package qrsimple.franz.com.qrsimple.domain.repository

import kotlinx.coroutines.flow.Flow
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType

interface QrHistoryRepository {
    fun getAllHistory(): Flow<List<QrItem>>
    fun getFavorites(): Flow<List<QrItem>>
    fun search(query: String): Flow<List<QrItem>>
    fun getByType(type: QrType): Flow<List<QrItem>>
    suspend fun getById(id: Long): QrItem?
    suspend fun saveQr(rawValue: String, title: String? = null, metadata: String? = null): Long
    suspend fun update(item: QrItem)
    suspend fun delete(item: QrItem)
    suspend fun deleteById(id: Long)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun updateTitle(id: Long, title: String)
    suspend fun clearHistory()
}

