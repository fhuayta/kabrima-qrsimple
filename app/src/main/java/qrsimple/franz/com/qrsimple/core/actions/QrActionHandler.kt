package qrsimple.franz.com.qrsimple.core.actions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.parser.ParsedQrContent
import qrsimple.franz.com.qrsimple.core.parser.QrParser
import qrsimple.franz.com.qrsimple.core.parser.WifiConfig
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType

object QrActionHandler {

    fun executePrimaryAction(context: Context, item: QrItem) {
        val parsed = QrParser.parse(item.rawValue)
        when (parsed) {
            is ParsedQrContent.Url -> openUrl(context, parsed.url)
            is ParsedQrContent.Wifi -> connectWifi(context, parsed.config)
            is ParsedQrContent.Contact -> addContact(context, parsed)
            is ParsedQrContent.Phone -> dialPhone(context, parsed.number)
            is ParsedQrContent.Sms -> sendSms(context, parsed.number, parsed.message)
            is ParsedQrContent.Email -> sendEmail(context, parsed.address, parsed.subject, parsed.body)
            is ParsedQrContent.Geo -> openMap(context, parsed.lat, parsed.lng, parsed.query)
            is ParsedQrContent.Text -> copyToClipboard(context, parsed.content)
        }
    }

    fun openUrl(context: Context, url: String) {
        try {
            val validUrl = if (!url.startsWith("http://", ignoreCase = true) &&
                !url.startsWith("https://", ignoreCase = true)
            ) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No se encontró un navegador compatible", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String, message: String? = null) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("QR Simple", text)
            clipboard.setPrimaryClip(clip)
            val toastMsg = message ?: context.getString(R.string.result_copied_toast)
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareContent(context: Context, text: String, title: String = "Compartir código QR") {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (_: Exception) {
            Toast.makeText(context, "No se pudo compartir el contenido", Toast.LENGTH_SHORT).show()
        }
    }

    fun connectWifi(context: Context, config: WifiConfig) {
        // Copy password to clipboard for convenience
        if (config.password.isNotBlank()) {
            copyToClipboard(context, config.password, "Contraseña de Wi-Fi copiada: ${config.password}")
        }
        try {
            // Open Wi-Fi settings
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Selecciona '${config.ssid}' en ajustes de Wi-Fi", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Error al abrir ajustes de Wi-Fi", Toast.LENGTH_SHORT).show()
        }
    }

    fun addContact(context: Context, contact: ParsedQrContent.Contact) {
        try {
            val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
                contact.phone?.let { putExtra(ContactsContract.Intents.Insert.PHONE, it) }
                contact.email?.let { putExtra(ContactsContract.Intents.Insert.EMAIL, it) }
                contact.organization?.let { putExtra(ContactsContract.Intents.Insert.COMPANY, it) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            contact.phone?.let { dialPhone(context, it) }
        }
    }

    fun dialPhone(context: Context, number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            copyToClipboard(context, number, "Número copiado: $number")
        }
    }

    fun sendSms(context: Context, number: String, message: String?) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).apply {
                message?.let { putExtra("sms_body", it) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            dialPhone(context, number)
        }
    }

    fun sendEmail(context: Context, address: String, subject: String?, body: String?) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address")).apply {
                subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                body?.let { putExtra(Intent.EXTRA_TEXT, it) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            copyToClipboard(context, address, "Email copiado: $address")
        }
    }

    fun openMap(context: Context, lat: Double, lng: Double, query: String?) {
        try {
            val uriStr = if (!query.isNullOrBlank()) {
                "geo:$lat,$lng?q=${Uri.encode(query)}"
            } else {
                "geo:$lat,$lng?q=$lat,$lng"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openUrl(context, "https://maps.google.com/?q=$lat,$lng")
        }
    }
}

