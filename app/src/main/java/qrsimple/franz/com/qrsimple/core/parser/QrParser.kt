package qrsimple.franz.com.qrsimple.core.parser

import java.net.URI
import java.util.Locale

object QrParser {

    fun parse(raw: String): ParsedQrContent {
        val trimmed = raw.trim()

        if (trimmed.isEmpty()) {
            return ParsedQrContent.Text("")
        }

        // 1. Wi-Fi: WIFI:S:MySSID;T:WPA;P:MyPassword;H:false;;
        if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
            return parseWifi(trimmed)
        }

        // 2. URLs (http://, https://, www., or valid web domain)
        if (isUrl(trimmed)) {
            val fullUrl = if (!trimmed.startsWith("http://", ignoreCase = true) &&
                !trimmed.startsWith("https://", ignoreCase = true)
            ) {
                "https://$trimmed"
            } else {
                trimmed
            }
            val host = extractHost(fullUrl)
            return ParsedQrContent.Url(url = fullUrl, host = host)
        }

        // 3. Contact (vCard / MeCard)
        if (trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) ||
            trimmed.startsWith("MECARD:", ignoreCase = true)
        ) {
            return parseContact(trimmed)
        }

        // 4. Phone: tel:+123456789 or raw phone number
        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val number = trimmed.substring(4).trim()
            return ParsedQrContent.Phone(number)
        }

        // 5. Email: mailto:user@domain.com or pure email format
        if (trimmed.startsWith("mailto:", ignoreCase = true)) {
            return parseMailto(trimmed)
        }
        if (isPureEmail(trimmed)) {
            return ParsedQrContent.Email(address = trimmed, subject = null, body = null)
        }

        // 6. SMS: sms:+12345?body=Hello or smsto:+12345:Hello
        if (trimmed.startsWith("sms:", ignoreCase = true) || trimmed.startsWith("smsto:", ignoreCase = true)) {
            return parseSms(trimmed)
        }

        // 7. Geo: geo:40.7128,-74.0060?q=New+York
        if (trimmed.startsWith("geo:", ignoreCase = true)) {
            val geo = parseGeo(trimmed)
            if (geo != null) return geo
        }

        // 8. Plain phone pattern (e.g. +34 600 123 456)
        if (isPhoneNumber(trimmed)) {
            return ParsedQrContent.Phone(trimmed)
        }

        // 9. Fallback: Text
        return ParsedQrContent.Text(trimmed)
    }

    private fun isUrl(text: String): Boolean {
        if (text.startsWith("http://", ignoreCase = true) ||
            text.startsWith("https://", ignoreCase = true) ||
            text.startsWith("www.", ignoreCase = true)
        ) {
            return true
        }
        // Check standard domain pattern like example.com/path
        val domainRegex = Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$")
        return domainRegex.matches(text) && !text.contains(" ") && !text.contains("@")
    }

    private fun extractHost(url: String): String {
        return try {
            val uri = URI(url)
            val host = uri.host ?: url
            host.removePrefix("www.")
        } catch (_: Exception) {
            url.substringBefore("/").removePrefix("http://").removePrefix("https://").removePrefix("www.")
        }
    }

    private fun parseWifi(raw: String): ParsedQrContent.Wifi {
        var ssid = ""
        var password = ""
        var encryption = "WPA"
        var isHidden = false

        // Example: WIFI:S:MySSID;T:WPA;P:MyPass;H:false;;
        val content = raw.substring(5).trimEnd(';')
        val tokens = content.split(";")

        for (token in tokens) {
            val parts = token.split(":", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].uppercase(Locale.ROOT)
                val value = parts[1]
                when (key) {
                    "S" -> ssid = value
                    "P" -> password = value
                    "T" -> encryption = value
                    "H" -> isHidden = value.equals("true", ignoreCase = true)
                }
            }
        }

        return ParsedQrContent.Wifi(
            WifiConfig(
                ssid = ssid,
                password = password,
                encryption = encryption,
                isHidden = isHidden
            )
        )
    }

    private fun parseContact(raw: String): ParsedQrContent.Contact {
        var name = ""
        var phone: String? = null
        var email: String? = null
        var org: String? = null

        val lines = raw.lines()
        for (line in lines) {
            val upper = line.uppercase(Locale.ROOT)
            when {
                upper.startsWith("FN:") -> name = line.substring(3).trim()
                upper.startsWith("N:") && name.isEmpty() -> {
                    val parts = line.substring(2).split(";")
                    name = parts.reversed().filter { it.isNotBlank() }.joinToString(" ")
                }
                upper.startsWith("TEL:") || upper.startsWith("TEL;") -> {
                    phone = line.substringAfter(":").trim()
                }
                upper.startsWith("EMAIL:") || upper.startsWith("EMAIL;") -> {
                    email = line.substringAfter(":").trim()
                }
                upper.startsWith("ORG:") -> {
                    org = line.substring(4).trim()
                }
            }
        }

        if (name.isEmpty() && raw.startsWith("MECARD:", ignoreCase = true)) {
            val tokens = raw.substring(7).trimEnd(';').split(";")
            for (token in tokens) {
                val parts = token.split(":", limit = 2)
                if (parts.size == 2) {
                    when (parts[0].uppercase(Locale.ROOT)) {
                        "N" -> name = parts[1]
                        "TEL" -> phone = parts[1]
                        "EMAIL" -> email = parts[1]
                        "ORG" -> org = parts[1]
                    }
                }
            }
        }

        return ParsedQrContent.Contact(
            name = name.ifBlank { "Contacto" },
            phone = phone,
            email = email,
            organization = org
        )
    }

    private fun parseMailto(raw: String): ParsedQrContent.Email {
        val withoutPrefix = raw.substring(7)
        val address = withoutPrefix.substringBefore("?")
        val query = if (withoutPrefix.contains("?")) withoutPrefix.substringAfter("?") else ""

        var subject: String? = null
        var body: String? = null

        if (query.isNotEmpty()) {
            val params = query.split("&")
            for (p in params) {
                val parts = p.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].lowercase(Locale.ROOT)
                    val value = parts[1]
                    if (key == "subject") subject = value
                    if (key == "body") body = value
                }
            }
        }

        return ParsedQrContent.Email(
            address = address,
            subject = subject,
            body = body
        )
    }

    private fun isPureEmail(text: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(text)
    }

    private fun parseSms(raw: String): ParsedQrContent.Sms {
        val clean = raw.removePrefix("smsto:").removePrefix("sms:").removePrefix("SMSTO:").removePrefix("SMS:")
        return if (clean.contains(":")) {
            val number = clean.substringBefore(":")
            val message = clean.substringAfter(":")
            ParsedQrContent.Sms(number = number, message = message.ifBlank { null })
        } else if (clean.contains("?")) {
            val number = clean.substringBefore("?")
            val body = clean.substringAfter("body=").substringBefore("&")
            ParsedQrContent.Sms(number = number, message = body.ifBlank { null })
        } else {
            ParsedQrContent.Sms(number = clean, message = null)
        }
    }

    private fun parseGeo(raw: String): ParsedQrContent.Geo? {
        val clean = raw.substring(4).trim()
        val coords = clean.substringBefore("?")
        val query = if (clean.contains("q=")) clean.substringAfter("q=").substringBefore("&") else null

        val parts = coords.split(",")
        if (parts.size >= 2) {
            val lat = parts[0].toDoubleOrNull()
            val lng = parts[1].toDoubleOrNull()
            if (lat != null && lng != null) {
                return ParsedQrContent.Geo(lat = lat, lng = lng, query = query)
            }
        }
        return null
    }

    private fun isPhoneNumber(text: String): Boolean {
        val phoneRegex = Regex("^\\+?[0-9\\s\\-()]{7,20}$")
        return phoneRegex.matches(text) && text.any { it.isDigit() }
    }
}

