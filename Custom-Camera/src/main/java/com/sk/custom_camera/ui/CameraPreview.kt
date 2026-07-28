package com.yourcompany.cameraui

import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Hosts the CameraX [PreviewView] inside Compose and wires up pinch-to-zoom
 * and tap-to-focus gestures against [controller].
 */
@Composable
internal fun CameraPreview(
    controller: CameraXController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    // Re-bind whenever the lens facing changes (or on first composition).
    LaunchedEffect(controller.lensFacing) {
        controller.bind(previewView)
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
            .pointerInput(controller) {
                detectTransformGestures { _, _, zoom, _ ->
                    controller.onPinchZoom(zoom)
                }
            }
            .pointerInput(controller) {
                detectTapGestures { offset ->
                    val factory = SurfaceOrientedMeteringPointFactory(
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                    val point = factory.createPoint(offset.x, offset.y)
                    controller.focusAndMeter(point)
                }
            }
    )
}
