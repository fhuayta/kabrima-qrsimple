package qrsimple.franz.com.qrsimple.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType

@Entity(tableName = "qr_history")
data class QrEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val title: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val metadata: String? = null
) {
    fun toDomain(): QrItem {
        return QrItem(
            id = id,
            rawValue = rawValue,
            title = title,
            type = QrType.fromString(type),
            timestamp = timestamp,
            isFavorite = isFavorite,
            metadata = metadata
        )
    }

    companion object {
        fun fromDomain(item: QrItem): QrEntity {
            return QrEntity(
                id = item.id,
                rawValue = item.rawValue,
                title = item.title,
                type = item.type.name,
                timestamp = item.timestamp,
                isFavorite = item.isFavorite,
                metadata = item.metadata
            )
        }
    }
}

