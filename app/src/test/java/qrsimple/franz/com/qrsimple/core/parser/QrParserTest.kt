package qrsimple.franz.com.qrsimple.core.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import qrsimple.franz.com.qrsimple.domain.model.QrType

class QrParserTest {

    @Test
    fun `parse https url returns Url with extracted host`() {
        val result = QrParser.parse("https://www.github.com/franz/PDFly")
        assertTrue(result is ParsedQrContent.Url)
        val url = result as ParsedQrContent.Url
        assertEquals("https://www.github.com/franz/PDFly", url.url)
        assertEquals("github.com", url.host)
        assertEquals("github.com", url.defaultTitle)
        assertEquals(QrType.URL, url.type)
    }

    @Test
    fun `parse domain without scheme adds https`() {
        val result = QrParser.parse("wikipedia.org/wiki/QR_code")
        assertTrue(result is ParsedQrContent.Url)
        val url = result as ParsedQrContent.Url
        assertEquals("https://wikipedia.org/wiki/QR_code", url.url)
        assertEquals("wikipedia.org", url.host)
    }

    @Test
    fun `parse wifi network extracts ssid, password and encryption`() {
        val result = QrParser.parse("WIFI:S:MiFibra-5G;T:WPA;P:ClaveSecreta2026;H:false;;")
        assertTrue(result is ParsedQrContent.Wifi)
        val wifi = result as ParsedQrContent.Wifi
        assertEquals("MiFibra-5G", wifi.config.ssid)
        assertEquals("ClaveSecreta2026", wifi.config.password)
        assertEquals("WPA", wifi.config.encryption)
        assertEquals(false, wifi.config.isHidden)
        assertEquals("Wi-Fi: MiFibra-5G", wifi.defaultTitle)
        assertEquals(QrType.WIFI, wifi.type)
    }

    @Test
    fun `parse phone number with tel prefix`() {
        val result = QrParser.parse("tel:+34600123456")
        assertTrue(result is ParsedQrContent.Phone)
        val phone = result as ParsedQrContent.Phone
        assertEquals("+34600123456", phone.number)
        assertEquals("+34600123456", phone.defaultTitle)
        assertEquals(QrType.PHONE, phone.type)
    }

    @Test
    fun `parse email with mailto and parameters`() {
        val result = QrParser.parse("mailto:soporte@kabrima.com?subject=Consulta&body=Hola")
        assertTrue(result is ParsedQrContent.Email)
        val email = result as ParsedQrContent.Email
        assertEquals("soporte@kabrima.com", email.address)
        assertEquals("Consulta", email.subject)
        assertEquals("Hola", email.body)
        assertEquals("soporte@kabrima.com", email.defaultTitle)
        assertEquals(QrType.EMAIL, email.type)
    }

    @Test
    fun `parse vcard contact extracts full name and phone`() {
        val vcard = """
            BEGIN:VCARD
            VERSION:3.0
            FN:Juan Pérez
            TEL:+34912345678
            EMAIL:juan@example.com
            ORG:Kabrima Tech
            END:VCARD
        """.trimIndent()
        val result = QrParser.parse(vcard)
        assertTrue(result is ParsedQrContent.Contact)
        val contact = result as ParsedQrContent.Contact
        assertEquals("Juan Pérez", contact.name)
        assertEquals("+34912345678", contact.phone)
        assertEquals("juan@example.com", contact.email)
        assertEquals("Kabrima Tech", contact.organization)
        assertEquals("Juan Pérez", contact.defaultTitle)
        assertEquals(QrType.CONTACT, contact.type)
    }

    @Test
    fun `parse sms returns sms parsed content`() {
        val result = QrParser.parse("smsto:+59170012345:Hola como estas")
        assertTrue(result is ParsedQrContent.Sms)
        val sms = result as ParsedQrContent.Sms
        assertEquals("+59170012345", sms.number)
        assertEquals("Hola como estas", sms.message)
        assertEquals("SMS: +59170012345", sms.defaultTitle)
        assertEquals(QrType.SMS, sms.type)
    }

    @Test
    fun `parse plain text returns text content`() {
        val result = QrParser.parse("Nota rápida: Comprar leche y pan")
        assertTrue(result is ParsedQrContent.Text)
        val text = result as ParsedQrContent.Text
        assertEquals("Nota rápida: Comprar leche y pan", text.content)
        assertEquals(QrType.TEXT, text.type)
    }
}

