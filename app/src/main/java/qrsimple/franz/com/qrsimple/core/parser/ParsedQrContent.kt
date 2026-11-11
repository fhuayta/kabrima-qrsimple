package qrsimple.franz.com.qrsimple.core.parser

import qrsimple.franz.com.qrsimple.domain.model.QrType

data class WifiConfig(
    val ssid: String,
    val password: String = "",
    val encryption: String = "WPA",
    val isHidden: Boolean = false
)

sealed class ParsedQrContent(val type: QrType, val defaultTitle: String) {
    data class Url(
        val url: String,
        val host: String
    ) : ParsedQrContent(QrType.URL, host)

    data class Wifi(
        val config: WifiConfig
    ) : ParsedQrContent(QrType.WIFI, "Wi-Fi: ${config.ssid.ifBlank { "Red" }}")

    data class Contact(
        val name: String,
        val phone: String? = null,
        val email: String? = null,
        val organization: String? = null
    ) : ParsedQrContent(QrType.CONTACT, name.ifBlank { "Contacto" })

    data class Phone(
        val number: String
    ) : ParsedQrContent(QrType.PHONE, number)

    data class Sms(
        val number: String,
        val message: String? = null
    ) : ParsedQrContent(QrType.SMS, "SMS: $number")

    data class Email(
        val address: String,
        val subject: String? = null,
        val body: String? = null
    ) : ParsedQrContent(QrType.EMAIL, address)

    data class Geo(
        val lat: Double,
        val lng: Double,
        val query: String? = null
    ) : ParsedQrContent(QrType.GEO, "Ubicación: $lat, $lng")

    data class Text(
        val content: String
    ) : ParsedQrContent(QrType.TEXT, content.take(32).trim().replace("\n", " "))
}

