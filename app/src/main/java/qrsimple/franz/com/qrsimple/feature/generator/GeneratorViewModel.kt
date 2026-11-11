package qrsimple.franz.com.qrsimple.feature.generator

import android.content.Context
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.generator.QrGenerator
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository

class GeneratorViewModel(
    private val qrRepository: QrHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    fun onTypeSelected(type: GeneratorType) {
        _uiState.update { it.copy(selectedType = type, generatedBitmap = null) }
    }

    fun onUrlChanged(value: String) {
        _uiState.update { it.copy(urlInput = value) }
    }

    fun onWifiSsidChanged(value: String) {
        _uiState.update { it.copy(wifiSsid = value) }
    }

    fun onWifiPasswordChanged(value: String) {
        _uiState.update { it.copy(wifiPassword = value) }
    }

    fun onWifiEncryptionChanged(value: String) {
        _uiState.update { it.copy(wifiEncryption = value) }
    }

    fun onTextChanged(value: String) {
        _uiState.update { it.copy(textInput = value) }
    }

    fun onWaCountryCodeChanged(value: String) {
        _uiState.update { it.copy(waCountryCode = value) }
    }

    fun onWaPhoneChanged(value: String) {
        _uiState.update { it.copy(waPhoneNumber = value) }
    }

    fun onWaMessageChanged(value: String) {
        _uiState.update { it.copy(waMessage = value) }
    }

    fun onEmailAddressChanged(value: String) {
        _uiState.update { it.copy(emailAddress = value) }
    }

    fun onEmailSubjectChanged(value: String) {
        _uiState.update { it.copy(emailSubject = value) }
    }

    fun onEmailBodyChanged(value: String) {
        _uiState.update { it.copy(emailBody = value) }
    }

    fun buildRawPayload(): String {
        val state = _uiState.value
        return when (state.selectedType) {
            GeneratorType.URL -> {
                val raw = state.urlInput.trim()
                if (!raw.startsWith("http://", ignoreCase = true) && !raw.startsWith("https://", ignoreCase = true)) {
                    "https://$raw"
                } else {
                    raw
                }
            }
            GeneratorType.WIFI -> {
                "WIFI:S:${state.wifiSsid.trim()};T:${state.wifiEncryption};P:${state.wifiPassword};;"
            }
            GeneratorType.TEXT -> {
                state.textInput.trim()
            }
            GeneratorType.WHATSAPP -> {
                val cleanCode = state.waCountryCode.filter { it.isDigit() }
                val cleanNumber = state.waPhoneNumber.filter { it.isDigit() }
                val fullNumber = "$cleanCode$cleanNumber"
                if (state.waMessage.isNotBlank()) {
                    val encodedMsg = URLEncoder.encode(state.waMessage.trim(), StandardCharsets.UTF_8.name())
                    "https://wa.me/$fullNumber?text=$encodedMsg"
                } else {
                    "https://wa.me/$fullNumber"
                }
            }
            GeneratorType.EMAIL -> {
                val email = state.emailAddress.trim()
                val params = mutableListOf<String>()
                if (state.emailSubject.isNotBlank()) {
                    val encodedSub = URLEncoder.encode(state.emailSubject.trim(), StandardCharsets.UTF_8.name()).replace("+", "%20")
                    params.add("subject=$encodedSub")
                }
                if (state.emailBody.isNotBlank()) {
                    val encodedBody = URLEncoder.encode(state.emailBody.trim(), StandardCharsets.UTF_8.name()).replace("+", "%20")
                    params.add("body=$encodedBody")
                }
                val queryString = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
                "mailto:$email$queryString"
            }
        }
    }

    fun generateQr() {
        val payload = buildRawPayload()
        if (payload.isBlank()) {
            _uiState.update { it.copy(errorToast = "Completa los campos necesarios para generar el QR") }
            return
        }

        _uiState.update { it.copy(isGenerating = true, errorToast = null) }
        viewModelScope.launch {
            val result = QrGenerator.generateQrBitmap(payload)
            result.fold(
                onSuccess = { bitmap ->
                    _uiState.update { it.copy(generatedBitmap = bitmap, isGenerating = false) }
                    // Save into local history as created QR
                    qrRepository.saveQr(
                        rawValue = payload,
                        title = when (_uiState.value.selectedType) {
                            GeneratorType.WIFI -> "Mi Wi-Fi: ${_uiState.value.wifiSsid}"
                            GeneratorType.WHATSAPP -> "WhatsApp: ${_uiState.value.waPhoneNumber}"
                            else -> null
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorToast = error.localizedMessage ?: "Error al generar código QR"
                        )
                    }
                }
            )
        }
    }

    fun saveToGallery(context: Context) {
        val bitmap = _uiState.value.generatedBitmap ?: return
        viewModelScope.launch {
            val result = QrGenerator.saveBitmapToGallery(context, bitmap)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(successToast = context.getString(R.string.generator_success_save)) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorToast = "${context.getString(R.string.generator_error_save)}: ${e.message}") }
                }
            )
        }
    }

    fun shareQr(context: Context) {
        val bitmap = _uiState.value.generatedBitmap ?: return
        viewModelScope.launch {
            val result = QrGenerator.shareBitmap(context, bitmap)
            result.onFailure { e ->
                _uiState.update { it.copy(errorToast = "No se pudo compartir: ${e.message}") }
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(successToast = null, errorToast = null) }
    }
}
