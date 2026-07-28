package com.yourcompany.cameraui

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Captures a photo with the given [imageCapture] use case and saves it to the
 * device's shared Pictures collection via MediaStore.
 *
 * @param albumName sub-folder under Pictures/ to save into.
 * @param onSaved   called on the main thread with the resulting content [Uri].
 * @param onError   called on the main thread if capture fails.
 */
fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    albumName: String = "CameraUiLibrary",
    onSaved: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val capture = imageCapture ?: return

    val name = "IMG_${
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    }.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$albumName")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let(onSaved)
            }

            override fun onError(exc: ImageCaptureException) {
                onError(exc)
            }
        }
    )
}
