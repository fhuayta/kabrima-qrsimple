package qrsimple.franz.com.qrsimple.feature.result

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.actions.QrActionHandler
import qrsimple.franz.com.qrsimple.core.parser.ParsedQrContent
import qrsimple.franz.com.qrsimple.core.parser.QrParser
import qrsimple.franz.com.qrsimple.core.theme.CyanAccent
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface
import qrsimple.franz.com.qrsimple.core.theme.DarkSurfaceVariant
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrResultBottomSheet(
    item: QrItem,
    onDismiss: () -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val parsed = remember(item.rawValue) { QrParser.parse(item.rawValue) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        contentColor = Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Type Icon container
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EmeraldPrimary.copy(alpha = 0.18f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getTypeIcon(item.type),
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        // Badge chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = stringResource(getTypeLabelRes(item.type)).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Favorite button
                val starTint by animateColorAsState(
                    targetValue = if (item.isFavorite) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.5f),
                    label = "star_color"
                )
                IconButton(
                    onClick = { onToggleFavorite(item.id, item.isFavorite) },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = stringResource(if (item.isFavorite) R.string.result_favorite_remove else R.string.result_favorite_add),
                        tint = starTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Body Card based on Content
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (parsed) {
                        is ParsedQrContent.Url -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (parsed.url.startsWith("https://", ignoreCase = true)) EmeraldPrimary else Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (parsed.url.startsWith("https://", ignoreCase = true)) "Sitio web cifrado seguro (HTTPS)" else "Sitio web estándar (HTTP)",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = parsed.url,
                                fontSize = 14.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 20.sp
                            )
                        }

                        is ParsedQrContent.Wifi -> {
                            Text(
                                text = "Nombre de Red (SSID):",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = parsed.config.ssid,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (parsed.config.password.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Contraseña:",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isPasswordVisible) parsed.config.password else "••••••••••••",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = EmeraldPrimary
                                    )
                                    IconButton(
                                        onClick = { isPasswordVisible = !isPasswordVisible },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Seguridad: ${parsed.config.encryption} ${if (parsed.config.isHidden) "(Oculta)" else ""}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        is ParsedQrContent.Contact -> {
                            Text(
                                text = parsed.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            parsed.phone?.let {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Teléfono: $it", fontSize = 14.sp, color = EmeraldPrimary)
                            }
                            parsed.email?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Email: $it", fontSize = 14.sp, color = CyanAccent)
                            }
                            parsed.organization?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Organización: $it", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        is ParsedQrContent.Phone -> {
                            Text(
                                text = "Número de Teléfono:",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = parsed.number,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }

                        is ParsedQrContent.Sms -> {
                            Text(
                                text = "Destinatario: ${parsed.number}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            parsed.message?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = it, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }

                        is ParsedQrContent.Email -> {
                            Text(
                                text = parsed.address,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            parsed.subject?.let {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Asunto: $it", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                            parsed.body?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = it, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        is ParsedQrContent.Geo -> {
                            Text(
                                text = "Coordenadas:",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${parsed.lat}, ${parsed.lng}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }

                        is ParsedQrContent.Text -> {
                            Text(
                                text = parsed.content,
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${parsed.content.length} caracteres",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action Button (Colored)
            Button(
                onClick = {
                    QrActionHandler.executePrimaryAction(context, item)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = getPrimaryActionIcon(item.type),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(getPrimaryActionLabelRes(item.type)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Actions (Copy & Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val textToCopy = when (parsed) {
                            is ParsedQrContent.Wifi -> parsed.config.password.ifBlank { parsed.config.ssid }
                            else -> item.rawValue
                        }
                        QrActionHandler.copyToClipboard(context, textToCopy)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.copy), fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = {
                        QrActionHandler.shareContent(context, item.rawValue)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.share), fontSize = 14.sp)
                }
            }
        }
    }
}

fun getTypeIcon(type: QrType): ImageVector {
    return when (type) {
        QrType.URL -> Icons.Default.Link
        QrType.WIFI -> Icons.Default.Wifi
        QrType.CONTACT -> Icons.Default.Person
        QrType.PHONE -> Icons.Default.Call
        QrType.SMS -> Icons.AutoMirrored.Filled.Chat
        QrType.EMAIL -> Icons.Default.Email
        QrType.GEO -> Icons.Default.LocationOn
        QrType.TEXT -> Icons.AutoMirrored.Filled.Notes
    }
}

fun getTypeLabelRes(type: QrType): Int {
    return when (type) {
        QrType.URL -> R.string.result_type_url
        QrType.WIFI -> R.string.result_type_wifi
        QrType.CONTACT -> R.string.result_type_contact
        QrType.PHONE -> R.string.result_type_phone
        QrType.SMS -> R.string.result_type_sms
        QrType.EMAIL -> R.string.result_type_email
        QrType.GEO -> R.string.result_type_geo
        QrType.TEXT -> R.string.result_type_text
    }
}

fun getPrimaryActionIcon(type: QrType): ImageVector {
    return when (type) {
        QrType.URL -> Icons.Default.OpenInBrowser
        QrType.WIFI -> Icons.Default.Wifi
        QrType.CONTACT -> Icons.Default.PersonAdd
        QrType.PHONE -> Icons.Default.Call
        QrType.SMS -> Icons.AutoMirrored.Filled.Chat
        QrType.EMAIL -> Icons.Default.Email
        QrType.GEO -> Icons.Default.LocationOn
        QrType.TEXT -> Icons.Default.ContentCopy
    }
}

fun getPrimaryActionLabelRes(type: QrType): Int {
    return when (type) {
        QrType.URL -> R.string.result_action_open_url
        QrType.WIFI -> R.string.result_action_connect_wifi
        QrType.CONTACT -> R.string.result_action_add_contact
        QrType.PHONE -> R.string.result_action_call
        QrType.SMS -> R.string.result_action_send_sms
        QrType.EMAIL -> R.string.result_action_send_email
        QrType.GEO -> R.string.result_action_open_maps
        QrType.TEXT -> R.string.result_action_copy_content
    }
}
