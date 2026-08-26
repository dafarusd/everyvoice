package org.everyvoice.aac.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Add/edit dialog for tiles.
 *
 * Caregivers think in labels and pictures, so the dialog asks for exactly
 * four things: what the button says, what the device speaks (if different),
 * an emoji, or a photo. The speak text defaults to the label.
 */
@Composable
fun TileEditDialog(
    title: String,
    initialLabel: String,
    initialSpeakText: String,
    initialIcon: String,
    confirmLabel: String,
    onConfirm: (label: String, speakText: String, icon: String, photo: Bitmap?) -> Unit,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)? = null,
    allowPhoto: Boolean = false,
) {
    var label by remember { mutableStateOf(initialLabel) }
    var speakText by remember { mutableStateOf(initialSpeakText) }
    var icon by remember { mutableStateOf(initialIcon) }
    var photo by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) photo = bitmap }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Button label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = speakText,
                    onValueChange = { speakText = it },
                    label = { Text("Spoken text (optional)") },
                    placeholder = { Text("Defaults to the label") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it.take(4) },
                    label = { Text("Emoji (optional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                if (allowPhoto) {
                    Row(modifier = Modifier.padding(top = 12.dp)) {
                        OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
                            Text(if (photo == null) "Take a photo" else "Retake photo")
                        }
                        photo?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Photo for this button",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .width(72.dp)
                                    .height(72.dp),
                            )
                        }
                    }
                }

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Delete this button", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(label, speakText, icon, photo) },
                enabled = label.isNotBlank(),
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
