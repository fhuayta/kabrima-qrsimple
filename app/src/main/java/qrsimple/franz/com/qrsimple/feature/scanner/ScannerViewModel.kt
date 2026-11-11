package qrsimple.franz.com.qrsimple.feature.scanner

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraControl
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qrsimple.franz.com.qrsimple.core.scanner.GalleryQrScanner
import qrsimple.franz.com.qrsimple.core.util.VibrationHelper
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository

class ScannerViewModel(
    private val qrRepository: QrHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var cameraControl: CameraControl? = null

    fun setCameraControl(control: CameraControl?) {
        this.cameraControl = control
        // Reapply torch and zoom state
        cameraControl?.enableTorch(_uiState.value.isTorchOn)
        cameraControl?.setLinearZoom(_uiState.value.zoomRatio)
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = isGranted) }
    }

    fun toggleTorch() {
        val newTorchState = !_uiState.value.isTorchOn
        cameraControl?.enableTorch(newTorchState)
        _uiState.update { it.copy(isTorchOn = newTorchState) }
    }

    fun toggleZoom() {
        val newZoom = if (_uiState.value.zoomRatio > 0.1f) 0.0f else 0.45f
        cameraControl?.setLinearZoom(newZoom)
        _uiState.update { it.copy(zoomRatio = newZoom) }
    }

    fun onQrDetected(raw: String, context: Context) {
        if (!_uiState.value.isScanningActive) return

        _uiState.update { it.copy(isScanningActive = false) }
        VibrationHelper.vibrateSuccess(context)

        viewModelScope.launch {
            val insertedId = qrRepository.saveQr(rawValue = raw)
            val item = qrRepository.getById(insertedId)
            _uiState.update { it.copy(scannedItem = item) }
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        _uiState.update { it.copy(isLoadingImage = true, errorMessage = null) }
        viewModelScope.launch {
            val result = GalleryQrScanner.scanUri(context, uri)
            result.fold(
                onSuccess = { raw ->
                    VibrationHelper.vibrateSuccess(context)
                    val insertedId = qrRepository.saveQr(rawValue = raw)
                    val item = qrRepository.getById(insertedId)
                    _uiState.update {
                        it.copy(
                            isLoadingImage = false,
                            isScanningActive = false,
                            scannedItem = item
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingImage = false,
                            errorMessage = error.localizedMessage ?: context.getString(qrsimple.franz.com.qrsimple.R.string.scanner_error_gallery)
                        )
                    }
                }
            )
        }
    }

    fun dismissResult() {
        _uiState.update {
            it.copy(
                scannedItem = null,
                isScanningActive = true
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun toggleFavorite(itemId: Long, currentFavorite: Boolean) {
        viewModelScope.launch {
            qrRepository.toggleFavorite(itemId, !currentFavorite)
            val updated = qrRepository.getById(itemId)
            _uiState.update { it.copy(scannedItem = updated) }
        }
    }
}

