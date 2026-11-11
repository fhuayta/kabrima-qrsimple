package qrsimple.franz.com.qrsimple.feature.scanner.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.theme.CyanAccent
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary

@Composable
fun ScannerTopBar(
    isTorchOn: Boolean,
    onToggleTorch: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenSupport: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title pill
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.45f),
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR Simple",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Action Buttons: Torch and Gallery
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Torch button
            val torchBg by animateColorAsState(
                targetValue = if (isTorchOn) EmeraldPrimary else Color.Black.copy(alpha = 0.45f),
                label = "torch_bg"
            )
            val torchTint by animateColorAsState(
                targetValue = if (isTorchOn) Color.Black else Color.White,
                label = "torch_tint"
            )

            IconButton(
                onClick = onToggleTorch,
                modifier = Modifier
                    .size(44.dp)
                    .background(torchBg, CircleShape)
            ) {
                Icon(
                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = stringResource(R.string.scanner_torch_desc),
                    tint = torchTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Gallery scan button
            IconButton(
                onClick = onOpenGallery,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = stringResource(R.string.scanner_gallery_desc),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // About & Legal button (Info)
            IconButton(
                onClick = onOpenAbout,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.about_action_desc),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Support project button (Heart)
            IconButton(
                onClick = onOpenSupport,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = stringResource(R.string.scanner_support_desc),
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun ZoomControlPill(
    zoomRatio: Float,
    onToggleZoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val is2x = zoomRatio > 0.1f

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onToggleZoom() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (!is2x) EmeraldPrimary else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1x",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!is2x) Color.Black else Color.White.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (is2x) EmeraldPrimary else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2x",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (is2x) Color.Black else Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

