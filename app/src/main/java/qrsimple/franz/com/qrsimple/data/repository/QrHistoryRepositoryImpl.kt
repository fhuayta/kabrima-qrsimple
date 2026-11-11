package qrsimple.franz.com.qrsimple.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import qrsimple.franz.com.qrsimple.core.parser.QrParser
import qrsimple.franz.com.qrsimple.data.local.dao.QrDao
import qrsimple.franz.com.qrsimple.data.local.entity.QrEntity
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository

class QrHistoryRepositoryImpl(
    private val qrDao: QrDao
) : QrHistoryRepository {

    override fun getAllHistory(): Flow<List<QrItem>> {
        return qrDao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override fun getFavorites(): Flow<List<QrItem>> {
        return qrDao.getFavorites().map { list -> list.map { it.toDomain() } }
    }

    override fun search(query: String): Flow<List<QrItem>> {
        return qrDao.search(query).map { list -> list.map { it.toDomain() } }
    }

    override fun getByType(type: QrType): Flow<List<QrItem>> {
        return qrDao.getByType(type.name).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(id: Long): QrItem? {
        return qrDao.getById(id)?.toDomain()
    }

    override suspend fun saveQr(rawValue: String, title: String?, metadata: String?): Long {
        val parsed = QrParser.parse(rawValue)
        val finalTitle = title?.ifBlank { null } ?: parsed.defaultTitle
        val entity = QrEntity(
            rawValue = rawValue,
            title = finalTitle,
            type = parsed.type.name,
            timestamp = System.currentTimeMillis(),
            isFavorite = false,
            metadata = metadata
        )
        return qrDao.insert(entity)
    }

    override suspend fun update(item: QrItem) {
        qrDao.update(QrEntity.fromDomain(item))
    }

    override suspend fun delete(item: QrItem) {
        qrDao.delete(QrEntity.fromDomain(item))
    }

    override suspend fun deleteById(id: Long) {
        qrDao.deleteById(id)
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        qrDao.updateFavorite(id, isFavorite)
    }

    override suspend fun updateTitle(id: Long, title: String) {
        qrDao.updateTitle(id, title)
    }

    override suspend fun clearHistory() {
        qrDao.clearAll()
    }
}
