package qrsimple.franz.com.qrsimple.domain.model

data class QrItem(
    val id: Long = 0,
    val rawValue: String,
    val title: String,
    val type: QrType,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val metadata: String? = null
)

