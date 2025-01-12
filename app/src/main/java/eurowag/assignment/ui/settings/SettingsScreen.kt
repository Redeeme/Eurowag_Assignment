package eurowag.assignment.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eurowag.assignment.ui.MainViewModel
import eurowag.assignment.database.MySharedPreferences
import eurowag.assignment.ui.navigation.Screen

@Composable
fun SettingsScreen(navController: NavController,mainViewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val prefs = MySharedPreferences(context)
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SettingsItem(
            text = "Set Interval",
            icon = Icons.Default.Timelapse,
            onClick = { showIntervalDialog = true }
        )

        SettingsItem(
            text = "Statistics",
            icon = Icons.Default.InsertChartOutlined,
            onClick = {navController.navigate(Screen.Stat.route)}
        )

        SettingsItem(
            text = "Share as json",
            icon = Icons.Default.Share,
            onClick = { mainViewModel.exportLocations(context) }
        )

        SettingsItem(
            text = "Wipe all data",
            icon = Icons.Default.DeleteOutline,
            onClick = { showDeleteDialog = true }
        )
    }

    NumberInputDialog(
        showDialog = showIntervalDialog,
        onDismiss = { showIntervalDialog = false },
        onConfirm = { number ->
            prefs.setInterval(number * 60000)
            mainViewModel.setInterval()
        },
        title = "current interval ${prefs.getInterval() / 60000} minute(s)",
        inputLabel = "Enter Interval (minutes)"
    )

    DeleteDialog(
        showDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = { mainViewModel.wipeData() }
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
    onConfirm: (Long) -> Unit,
    title: String,
    inputLabel: String,
    initialValue: String = ""
) {
    if (showDialog) {
        var inputValue by remember { mutableStateOf(initialValue) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title, fontSize = 14.sp) },
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
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = inputLabel, fontSize = 14.sp) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        inputValue.toLongOrNull()?.let { onConfirm(it) }
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

@Composable
fun DeleteDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete all location data? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}