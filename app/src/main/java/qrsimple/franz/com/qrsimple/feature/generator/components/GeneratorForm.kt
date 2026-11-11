package qrsimple.franz.com.qrsimple.feature.generator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface
import qrsimple.franz.com.qrsimple.core.theme.DarkSurfaceVariant
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorType
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorUiState
import qrsimple.franz.com.qrsimple.feature.generator.GeneratorViewModel

@Composable
fun GeneratorForm(
    uiState: GeneratorUiState,
    viewModel: GeneratorViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (uiState.selectedType) {
                GeneratorType.URL -> {
                    OutlinedTextField(
                        value = uiState.urlInput,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_url)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_url)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )
                }

                GeneratorType.WIFI -> {
                    OutlinedTextField(
                        value = uiState.wifiSsid,
                        onValueChange = { viewModel.onWifiSsidChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_ssid)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_ssid)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )

                    var isPasswordVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = uiState.wifiPassword,
                        onValueChange = { viewModel.onWifiPasswordChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_password)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_password)) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )

                    // Encryption chips
                    Text(
                        text = stringResource(R.string.generator_label_security) + ":",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("WPA", "WEP", "nopass").forEach { enc ->
                            val label = if (enc == "nopass") "Abierta" else enc
                            val isSelected = uiState.wifiEncryption == enc
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onWifiEncryptionChanged(enc) },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = Color.White.copy(alpha = 0.7f),
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.25f),
                                    selectedLabelColor = EmeraldPrimary
                                )
                            )
                        }
                    }
                }

                GeneratorType.TEXT -> {
                    OutlinedTextField(
                        value = uiState.textInput,
                        onValueChange = { viewModel.onTextChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_text)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_text)) },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )
                }

                GeneratorType.WHATSAPP -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.waCountryCode,
                            onValueChange = { viewModel.onWaCountryCodeChanged(it) },
                            label = { Text("DDI") },
                            placeholder = { Text("+34") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.width(90.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = getTextFieldColors()
                        )

                        OutlinedTextField(
                            value = uiState.waPhoneNumber,
                            onValueChange = { viewModel.onWaPhoneChanged(it) },
                            label = { Text(stringResource(R.string.generator_label_phone)) },
                            placeholder = { Text(stringResource(R.string.generator_hint_phone)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = getTextFieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = uiState.waMessage,
                        onValueChange = { viewModel.onWaMessageChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_wa_message)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_wa_message)) },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )
                }

                GeneratorType.EMAIL -> {
                    OutlinedTextField(
                        value = uiState.emailAddress,
                        onValueChange = { viewModel.onEmailAddressChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_email_to)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_email_to)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )

                    OutlinedTextField(
                        value = uiState.emailSubject,
                        onValueChange = { viewModel.onEmailSubjectChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_email_subject)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_email_subject)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )

                    OutlinedTextField(
                        value = uiState.emailBody,
                        onValueChange = { viewModel.onEmailBodyChanged(it) },
                        label = { Text(stringResource(R.string.generator_label_email_body)) },
                        placeholder = { Text(stringResource(R.string.generator_hint_email_body)) },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = getTextFieldColors()
                    )
                }
            }
        }
    }
}

@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmeraldPrimary,
    focusedLabelColor = EmeraldPrimary,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
