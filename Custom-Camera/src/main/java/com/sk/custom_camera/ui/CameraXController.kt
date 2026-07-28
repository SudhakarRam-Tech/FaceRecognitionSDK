package com.yourcompany.cameraui

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Public flash options exposed to consumers of the library.
 */
enum class CameraFlashMode {
    OFF, ON, AUTO
}

/**
 * Holds all mutable camera state (lens facing, flash, zoom, torch) and owns the
 * CameraX binding lifecycle. Create one with [rememberCameraXController] inside a
 * composable and pass it to [CameraCaptureScreen].
 */
@Stable
class CameraXController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)
        private set

    var flashMode by mutableStateOf(CameraFlashMode.OFF)
        private set

    var zoomRatio by mutableStateOf(1f)
        private set

    var minZoomRatio: Float = 1f
        private set

    var maxZoomRatio: Float = 1f
        private set

    var isReady by mutableStateOf(false)
        private set

    internal var imageCapture: ImageCapture? = null
        private set

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    internal suspend fun bind(previewView: PreviewView) {
        val provider = cameraProvider ?: getCameraProvider(context).also { cameraProvider = it }

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val capture = ImageCapture.Builder()
            .setFlashMode(flashMode.toImageCaptureFlashMode())
            .build()

        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        provider.unbindAll()
        camera = provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
        imageCapture = capture

        camera?.cameraInfo?.zoomState?.value?.let { state ->
            minZoomRatio = state.minZoomRatio
            maxZoomRatio = state.maxZoomRatio
            zoomRatio = state.zoomRatio
        }

        isReady = true
    }

    fun toggleLensFacing() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun updateFlashMode(mode: CameraFlashMode) {
        flashMode = mode
        imageCapture?.flashMode = mode.toImageCaptureFlashMode()
    }

    fun cycleFlashMode() {
        val next = when (flashMode) {
            CameraFlashMode.OFF -> CameraFlashMode.ON
            CameraFlashMode.ON -> CameraFlashMode.AUTO
            CameraFlashMode.AUTO -> CameraFlashMode.OFF
        }
        updateFlashMode(next)
    }

    fun updateZoomRatio(ratio: Float) {
        val clamped = ratio.coerceIn(minZoomRatio, maxZoomRatio)
        camera?.cameraControl?.setZoomRatio(clamped)
        zoomRatio = clamped
    }

    fun onPinchZoom(scaleFactor: Float) {
        updateZoomRatio(zoomRatio * scaleFactor)
    }

    fun focusAndMeter(meteringPoint: androidx.camera.core.MeteringPoint) {
        val action = androidx.camera.core.FocusMeteringAction.Builder(meteringPoint).build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }
}

private fun CameraFlashMode.toImageCaptureFlashMode(): Int = when (this) {
    CameraFlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
    CameraFlashMode.ON -> ImageCapture.FLASH_MODE_ON
    CameraFlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
}

internal suspend fun getCameraProvider(context: Context): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(context).also { future ->
            future.addListener(
                { continuation.resume(future.get()) },
                ContextCompat.getMainExecutor(context)
            )
        }
    }
