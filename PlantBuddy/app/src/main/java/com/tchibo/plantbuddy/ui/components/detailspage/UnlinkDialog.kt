package com.tchibo.plantbuddy.ui.components.detailspage

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tchibo.plantbuddy.R

@Composable
fun UnlinkDialog (
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.unlink_dialog_title)) },
        text = { Text(stringResource(id = R.string.unlink_dialog_content)) },
        confirmButton = {
            Button(
                onClick = onOk
            ) {
                Text("Unlink")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}