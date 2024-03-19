package com.tchibo.plantbuddy.ui.components.raspberrysettingspage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tchibo.plantbuddy.utils.TEXT_SIZE_NORMAL

@Composable
fun PropertyEditingComponent(
    labelText: String,
    textFieldValue: String?,
    editingValue: Boolean,
    onEditClick: () -> Unit,
    onCancelEditClick: () -> Unit,
    onConfirmEditClick: (String) -> Unit,
    confirmIcon: ImageVector,
    cancelIcon: ImageVector,
    editIcon: ImageVector,
) {

    var editedValue by remember {
        mutableStateOf(textFieldValue.orEmpty())
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 5.dp),
    ) {
        Text(
            text = labelText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 5.dp),
            fontSize = TEXT_SIZE_NORMAL,
            color = Color.White,
        )

        if (editingValue) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    value = editedValue,
                    onValueChange = {
                        editedValue = it
                    },
                )

                IconButton(
                    modifier = Modifier
                        .weight(0.15f),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = { onCancelEditClick() }
                ) {
                    Icon(
                        imageVector = cancelIcon,
                        contentDescription = "Cancel",
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(0.15f),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = { onConfirmEditClick(editedValue) }
                ) {
                    Icon(
                        imageVector = confirmIcon,
                        contentDescription = "Confirm",
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.85f),
                    value = textFieldValue.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                )

                IconButton(
                    modifier = Modifier
                        .weight(0.15f),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        editedValue = textFieldValue.orEmpty()
                        onEditClick()
                    }
                ) {
                    Icon(
                        imageVector = editIcon,
                        contentDescription = "Edit",
                    )
                }
            }
        }
    }
}