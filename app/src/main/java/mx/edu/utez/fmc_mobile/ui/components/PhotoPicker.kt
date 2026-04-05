package mx.edu.utez.fmc_mobile.ui.components

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import coil.compose.AsyncImage
import mx.edu.utez.fmc_mobile.ui.theme.FMC_MobileTheme
import mx.edu.utez.fmc_mobile.ui.theme.Light
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import java.io.File

private const val MAX_IMAGES = 3

@Composable
fun PhotoPicker(
    images: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isFull = images.size >= MAX_IMAGES

    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingUri?.let { uri ->
                val combined = (images + uri).take(MAX_IMAGES)
                onImagesSelected(combined)
            }
        }
        pendingUri = null
    }

    // Lanzador del permiso de cámara — abre la cámara si se concede
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoDir = File(context.cacheDir, "camera_photos").also { it.mkdirs() }
            val photoFile = File(photoDir, "photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            pendingUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val alreadyGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            val photoDir = File(context.cacheDir, "camera_photos").also { it.mkdirs() }
            val photoFile = File(photoDir, "photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            pendingUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        // Slot 1: add button o última imagen si está lleno
        Box(modifier = Modifier.size(90.dp)) {
            if (isFull) {
                ImageSlot(
                    uri = images[MAX_IMAGES - 1],
                    onRemove = {
                        onImagesSelected(images.dropLast(1))
                    }
                )
            } else {
                AddSlot(onClick = { launchCamera() })
            }
        }

        // Slots fijos de imágenes
        repeat(MAX_IMAGES - 1) { index ->
            Box(modifier = Modifier.size(90.dp)) {
                if (index < images.size) {
                    ImageSlot(
                        uri = images[index],
                        onRemove = {
                            onImagesSelected(images.toMutableList().also { it.removeAt(index) })
                        }
                    )
                } else {
                    EmptySlot()
                }
            }
        }
    }
}

@Composable
private fun AddSlot(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Light.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(2.dp, Primary),
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Tomar foto",
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun EmptySlot() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Light),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Light,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun ImageSlot(uri: Uri, onRemove: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
        )
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clickable { onRemove() }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.padding(3.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PhotoPickerPreview() {
    FMC_MobileTheme {
        PhotoPicker(
            images = emptyList(),
            onImagesSelected = {}
        )
    }
}
