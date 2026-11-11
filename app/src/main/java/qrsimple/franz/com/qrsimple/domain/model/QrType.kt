package qrsimple.franz.com.qrsimple.domain.model

enum class QrType {
    URL,
    WIFI,
    CONTACT,
    PHONE,
    SMS,
    EMAIL,
    GEO,
    TEXT;

    companion object {
        fun fromString(value: String): QrType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: TEXT
        }
    }
}

