package com.yourcompany.cameraui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun rememberCameraXController(): CameraXController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember { CameraXController(context, lifecycleOwner) }
}
