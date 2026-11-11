package qrsimple.franz.com.qrsimple.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.feature.result.QrResultBottomSheet
import qrsimple.franz.com.qrsimple.feature.scanner.components.CameraPermissionRequest
import qrsimple.franz.com.qrsimple.feature.scanner.components.CameraPreview
import qrsimple.franz.com.qrsimple.feature.scanner.components.ScannerOverlay
import qrsimple.franz.com.qrsimple.feature.scanner.components.ScannerTopBar
import qrsimple.franz.com.qrsimple.feature.scanner.components.ZoomControlPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onOpenSupport: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Check camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    // Gallery image picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(context, uri)
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onPermissionResult(hasPermission)
    }

    // Show error message via snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.hasCameraPermission) {
                // Camera Preview Feed
                CameraPreview(
                    isScanningEnabled = uiState.isScanningActive,
                    onQrDetected = { raw ->
                        viewModel.onQrDetected(raw, context)
                    },
                    onCameraControlReady = { control ->
                        viewModel.setCameraControl(control)
                    }
                )

                // Viewfinder Reticle Overlay
                ScannerOverlay(
                    isScanningActive = uiState.isScanningActive
                )

                // Top Controls (Torch, Gallery, Support, About)
                ScannerTopBar(
                    isTorchOn = uiState.isTorchOn,
                    onToggleTorch = { viewModel.toggleTorch() },
                    onOpenGallery = { galleryLauncher.launch("image/*") },
                    onOpenSupport = onOpenSupport,
                    onOpenAbout = onOpenAbout,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Zoom Control Pill
                ZoomControlPill(
                    zoomRatio = uiState.zoomRatio,
                    onToggleZoom = { viewModel.toggleZoom() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 36.dp)
                )
            } else {
                CameraPermissionRequest(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onOpenGallery = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            // Loading overlay when processing image from gallery
            if (uiState.isLoadingImage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmeraldPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Smart Result Bottom Sheet
            uiState.scannedItem?.let { item ->
                QrResultBottomSheet(
                    item = item,
                    onDismiss = { viewModel.dismissResult() },
                    onToggleFavorite = { id, currentFav ->
                        viewModel.toggleFavorite(id, currentFav)
                    }
                )
            }
        }
    }
}
