package qrsimple.franz.com.qrsimple.feature.generator

import android.graphics.Bitmap

data class GeneratorUiState(
    val selectedType: GeneratorType = GeneratorType.URL,
    // URL
    val urlInput: String = "",
    // Wi-Fi
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiEncryption: String = "WPA",
    // Text
    val textInput: String = "",
    // WhatsApp
    val waCountryCode: String = "+34",
    val waPhoneNumber: String = "",
    val waMessage: String = "",
    // Email
    val emailAddress: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    // State
    val generatedBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val successToast: String? = null,
    val errorToast: String? = null
)

