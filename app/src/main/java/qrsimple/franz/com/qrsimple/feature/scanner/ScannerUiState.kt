package qrsimple.franz.com.qrsimple.feature.scanner

import qrsimple.franz.com.qrsimple.domain.model.QrItem

data class ScannerUiState(
    val hasCameraPermission: Boolean = false,
    val isTorchOn: Boolean = false,
    val zoomRatio: Float = 0f, // 0f = 1x, 0.4f = 2x approx
    val isScanningActive: Boolean = true,
    val scannedItem: QrItem? = null,
    val isLoadingImage: Boolean = false,
    val errorMessage: String? = null
)

