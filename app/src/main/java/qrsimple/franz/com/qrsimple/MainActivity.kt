package qrsimple.franz.com.qrsimple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import qrsimple.franz.com.qrsimple.core.theme.DarkBackground
import qrsimple.franz.com.qrsimple.core.theme.QRSimpleTheme
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorViewModel
import qrsimple.franz.com.qrsimple.feature.history.HistoryViewModel
import qrsimple.franz.com.qrsimple.feature.scanner.ScannerViewModel

class MainActivity : ComponentActivity() {

    private val scannerViewModel: ScannerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as QRSimpleApplication
                return ScannerViewModel(app.qrRepository) as T
            }
        }
    }

    private val generatorViewModel: GeneratorViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as QRSimpleApplication
                return GeneratorViewModel(app.qrRepository) as T
            }
        }
    }

    private val historyViewModel: HistoryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as QRSimpleApplication
                return HistoryViewModel(app.qrRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as QRSimpleApplication).billingManager.startConnection()
        enableEdgeToEdge()
        setContent {
            QRSimpleTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MainScreen(
                        scannerViewModel = scannerViewModel,
                        generatorViewModel = generatorViewModel,
                        historyViewModel = historyViewModel
                    )
                }
            }
        }
    }
}
