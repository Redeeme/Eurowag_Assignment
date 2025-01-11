package eurowag.assignment.layouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eurowag.assignment.MainViewModel
import eurowag.assignment.layouts.navigation.Screen

@Composable
fun SettingsScreen(navController: NavController,mainViewModel: MainViewModel = hiltViewModel()){
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SettingsItem(
            text = "Set Interval",
            icon = Icons.Default.Timelapse,
            onClick = {showDialog = true}
        )

        SettingsItem(
            text = "Statistics",
            icon = Icons.Default.InsertChartOutlined,
            onClick = {}
        )

        SettingsItem(
            text = "Share as json",
            icon = Icons.Default.Share,
            onClick = {}
        )
    }
    NumberInputDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { number ->
            // Handle the number input
            println("Entered number: $number")
        },
        title = "Enter Interval (minutes)"
    )
}



@Composable
fun SettingsItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
fun NumberInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    title: String = "Enter Value",
    initialValue: String = ""
) {
    if (showDialog) {
        var inputValue by remember { mutableStateOf(initialValue) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { newValue ->
                        // Only allow numbers
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            inputValue = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        inputValue.toIntOrNull()?.let { onConfirm(it) }
                        onDismiss()
                    },
                    enabled = inputValue.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}