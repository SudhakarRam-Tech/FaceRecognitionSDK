package com.sk.custom_camera.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yourcompany.cameraui.CameraFlashMode
import com.yourcompany.cameraui.CameraPreview
import com.yourcompany.cameraui.ShutterButton
import com.yourcompany.cameraui.capturePhoto
import com.yourcompany.cameraui.rememberCameraXController
import org.w3c.dom.Text

/**
 * Drop-in, fully self-contained camera capture screen.
 *
 * Usage from a consuming app:
 * ```
 * CameraCaptureScreen(
 *     onImageCaptured = { uri -> /* handle saved photo */ },
 *     onError = { exception -> /* handle failure */ }
 * )
 * ```
 *
 * Handles the runtime CAMERA permission request itself; pass [onPermissionDenied]
 * if you want to react when the user declines.
 */

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onImageCaptured: (Uri) -> Unit = {},
    onError: (ImageCaptureException) -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(isCameraPermissionGranted(context))
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }


    Box(modifier = modifier.fillMaxSize()) {
        if (hasPermission) {
            CameraCaptureContent(
                onImageCaptured = onImageCaptured,
                onError = onError
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera permission is required",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

private fun isCameraPermissionGranted(context: android.content.Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

/**
 * The actual preview + controls, assumes permission has already been granted.
 * Exposed separately in case a consuming app wants to manage permissions itself.
 */
@Composable
fun CameraCaptureContent(
    modifier: Modifier = Modifier,
    onImageCaptured: (Uri) -> Unit = {},
    onError: (ImageCaptureException) -> Unit = {}
) {
    val context = LocalContext.current
    val controller = rememberCameraXController()

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        // Top bar: flash toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { controller.cycleFlashMode() }) {
                Icon(
                    imageVector = when (controller.flashMode) {
                        CameraFlashMode.OFF -> {
                            Toast.makeText(LocalContext.current,"Clicked", Toast.LENGTH_SHORT).show()
                            Icons.Default.FlashOff
                        }
                        CameraFlashMode.ON -> Icons.Default.FlashOn
                        CameraFlashMode.AUTO -> Icons.Default.FlashAuto
                    },
                    contentDescription = "Toggle flash",
                    tint = Color.White
                )
            }
        }

        // Bottom bar: shutter + switch camera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(48.dp))

            ShutterButton(
                onClick = {
                    capturePhoto(
                        context = context,
                        imageCapture = controller.imageCapture,
                        onSaved = onImageCaptured,
                        onError = onError
                    )
                }
            )

            IconButton(onClick = { controller.toggleLensFacing() }) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White,
                    modifier = Modifier
                )
            }
        }
    }
}
