package qrsimple.franz.com.qrsimple

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.core.theme.DarkBackground
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.feature.about.AboutDialog
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorScreen
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorViewModel
import qrsimple.franz.com.qrsimple.feature.history.HistoryScreen
import qrsimple.franz.com.qrsimple.feature.history.HistoryViewModel
import qrsimple.franz.com.qrsimple.feature.scanner.ScannerScreen
import qrsimple.franz.com.qrsimple.feature.scanner.ScannerViewModel
import qrsimple.franz.com.qrsimple.feature.support.SupportProjectDialog

enum class NavigationTab(val labelRes: Int) {
    SCANNER(R.string.nav_scanner),
    GENERATOR(R.string.nav_generator),
    HISTORY(R.string.nav_history)
}

@Composable
fun MainScreen(
    scannerViewModel: ScannerViewModel,
    generatorViewModel: GeneratorViewModel,
    historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as QRSimpleApplication
    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by rememberSaveable { mutableStateOf(NavigationTab.SCANNER) }
    var showSupportDialog by rememberSaveable { mutableStateOf(false) }
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }

    val tipOptions by app.billingManager.tipOptions.collectAsState()
    val hasSupported by app.preferencesRepository.hasSupportedProject.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        app.billingManager.purchaseSuccessEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        app.billingManager.purchaseErrorEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = Color.White
            ) {
                // Tab 1: Escanear
                val scannerLabel = stringResource(NavigationTab.SCANNER.labelRes)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.SCANNER,
                    onClick = { currentTab = NavigationTab.SCANNER },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = scannerLabel
                        )
                    },
                    label = {
                        Text(
                            text = scannerLabel,
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == NavigationTab.SCANNER) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )

                // Tab 2: Crear QR
                val generatorLabel = stringResource(NavigationTab.GENERATOR.labelRes)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.GENERATOR,
                    onClick = { currentTab = NavigationTab.GENERATOR },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = generatorLabel
                        )
                    },
                    label = {
                        Text(
                            text = generatorLabel,
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == NavigationTab.GENERATOR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )

                // Tab 3: Historial
                val historyLabel = stringResource(NavigationTab.HISTORY.labelRes)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.HISTORY,
                    onClick = { currentTab = NavigationTab.HISTORY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = historyLabel
                        )
                    },
                    label = {
                        Text(
                            text = historyLabel,
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == NavigationTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    NavigationTab.SCANNER -> {
                        ScannerScreen(
                            viewModel = scannerViewModel,
                            onOpenSupport = { showSupportDialog = true },
                            onOpenAbout = { showAboutDialog = true }
                        )
                    }
                    NavigationTab.GENERATOR -> {
                        GeneratorScreen(
                            viewModel = generatorViewModel,
                            onOpenSupport = { showSupportDialog = true },
                            onOpenAbout = { showAboutDialog = true }
                        )
                    }
                    NavigationTab.HISTORY -> {
                        HistoryScreen(
                            viewModel = historyViewModel,
                            onOpenSupport = { showSupportDialog = true },
                            onOpenAbout = { showAboutDialog = true }
                        )
                    }
                }
            }

            if (showSupportDialog) {
                SupportProjectDialog(
                    tipOptions = tipOptions,
                    hasSupported = hasSupported,
                    onSelectTip = { productId ->
                        (context as? Activity)?.let { activity ->
                            app.billingManager.launchBillingFlow(activity, productId)
                        }
                    },
                    onDismiss = { showSupportDialog = false }
                )
            }

            if (showAboutDialog) {
                AboutDialog(
                    onDismiss = { showAboutDialog = false },
                    onOpenSupport = {
                        showAboutDialog = false
                        showSupportDialog = true
                    }
                )
            }
        }
    }
}
