package qrsimple.franz.com.qrsimple.feature.history.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface

@Composable
fun ClearHistoryDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.history_dialog_clear_confirm), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.history_dialog_clear_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.history_dialog_clear_message),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        containerColor = DarkSurface,
        titleContentColor = Color.White,
        textContentColor = Color.White.copy(alpha = 0.8f)
    )
}
