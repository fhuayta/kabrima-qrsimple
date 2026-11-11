package qrsimple.franz.com.qrsimple.feature.generator.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorType

@Composable
fun GeneratorTypeSelector(
    selectedType: GeneratorType,
    onTypeSelected: (GeneratorType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GeneratorType.entries.forEach { type ->
            val isSelected = type == selectedType

            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                leadingIcon = {
                    Icon(
                        imageVector = getGeneratorIcon(type),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(type.labelRes),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = DarkSurface,
                    labelColor = Color.White.copy(alpha = 0.75f),
                    iconColor = Color.White.copy(alpha = 0.75f),
                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.22f),
                    selectedLabelColor = EmeraldPrimary,
                    selectedLeadingIconColor = EmeraldPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.White.copy(alpha = 0.12f),
                    selectedBorderColor = EmeraldPrimary,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}

fun getGeneratorIcon(type: GeneratorType): ImageVector {
    return when (type) {
        GeneratorType.URL -> Icons.Default.Link
        GeneratorType.WIFI -> Icons.Default.Wifi
        GeneratorType.TEXT -> Icons.AutoMirrored.Filled.Notes
        GeneratorType.WHATSAPP -> Icons.AutoMirrored.Filled.Chat
        GeneratorType.EMAIL -> Icons.Default.Email
    }
}

