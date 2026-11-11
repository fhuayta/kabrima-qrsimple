package qrsimple.franz.com.qrsimple.core.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrImageAnalyzer(
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_AZTEC
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)
    private var isScanningEnabled = true
    private var lastScannedTimestamp = 0L
    private val cooldownMs = 1200L

    fun setScanningEnabled(enabled: Boolean) {
        isScanningEnabled = enabled
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val currentTime = System.currentTimeMillis()

        if (!isScanningEnabled || mediaImage == null || (currentTime - lastScannedTimestamp) < cooldownMs) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }
                if (barcode != null) {
                    val raw = barcode.rawValue
                    if (!raw.isNullOrBlank() && isScanningEnabled) {
                        lastScannedTimestamp = System.currentTimeMillis()
                        onQrDetected(raw)
                    }
                }
            }
            .addOnFailureListener {
                // Continue stream even if frame fails
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

