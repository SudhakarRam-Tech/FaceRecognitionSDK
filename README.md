# Custom Camera UI

A drop-in, fully custom camera capture UI for Jetpack Compose, built on top of
CameraX. Handles runtime permissions, live preview, shutter, flash, front/back
switch, pinch-to-zoom, tap-to-focus, and a post-capture review screen — all
out of the box, and all restylable.

## Features

- 📷 Live camera preview inside Compose (CameraX `PreviewView` wrapped in `AndroidView`)
- 🔘 Restylable shutter button
- ⚡ Flash cycling: off → on → auto
- 🔄 Front/back camera switch
- 🤏 Pinch-to-zoom and tap-to-focus gestures
- 🖼️ Post-capture review screen (Retake / Use Photo) — optional, toggle with `enablePreview`
- 🔐 Built-in runtime CAMERA permission handling
- 💾 Captured photos saved to MediaStore automatically

## Requirements

- Android `minSdk` 21+
- Kotlin 2.0+ (uses the Compose Compiler Gradle plugin)
- Jetpack Compose

## Installation

Add JitPack to your **root** `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency to your **app-level** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.SudhakarRam-Tech:Custom-Camera:1.0.2")
}
```

> Replace the coordinates above with whatever your JitPack build actually
> published — check your JitPack build log or
> `https://jitpack.io/#SudhakarRam-Tech/Custom-Camera` for the exact
> group/artifact/version.

## Permissions

The library's manifest already declares:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.any" android:required="false" />
```

These merge automatically into your app — no manual manifest edits needed.
Runtime permission is requested by the composable itself.

## Usage

### Basic — everything handled for you

```kotlin
import com.sk.custom_camera.ui.CameraCaptureScreen

setContent {
    MaterialTheme {
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                // called after the user reviews and taps "Use Photo"
            },
            onError = { exception ->
                // handle ImageCaptureException
            },
            onPermissionDenied = {
                // user declined the CAMERA permission
            }
        )
    }
}
```

### Skip the review screen

By default, tapping the shutter shows a full-screen review (Retake / Use
Photo) before `onImageCaptured` fires. To get the callback immediately on
capture instead:

```kotlin
CameraCaptureScreen(
    enablePreview = false,
    onImageCaptured = { uri -> /* fires immediately */ }
)
```

### Managing permissions yourself

If your app already handles the CAMERA permission, skip the built-in flow
and use the lower-level composable directly:

```kotlin
CameraCaptureContent(
    enablePreview = true,
    onImageCaptured = { uri -> /* ... */ },
    onError = { exception -> /* ... */ }
)
```

### Fully custom layout

For full control over the UI, build your own screen around the exposed
building blocks:

```kotlin
val controller = rememberCameraXController()

Box(Modifier.fillMaxSize()) {
    CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

    ShutterButton(
        onClick = {
            capturePhoto(
                context = context,
                imageCapture = controller.imageCapture,
                onSaved = { uri -> /* ... */ },
                onError = { exception -> /* ... */ }
            )
        }
    )
}
```

`controller` exposes `lensFacing`, `flashMode`, `zoomRatio`,
`toggleLensFacing()`, `cycleFlashMode()`, `updateZoomRatio()`, and
`focusAndMeter()` if you want to drive your own controls.

## API reference

| Composable | Purpose |
|---|---|
| `CameraCaptureScreen` | Full screen: permission handling + preview + controls + review |
| `CameraCaptureContent` | Same as above, minus permission handling |
| `CameraPreview` | Just the live camera surface + pinch/tap gestures |
| `ShutterButton` | Standalone, restylable shutter button |
| `PhotoPreviewScreen` | Standalone Retake/Use Photo review screen |
| `rememberCameraXController()` | Creates the camera state/controller |
| `capturePhoto(...)` | Function to trigger capture + save to MediaStore |

## Using the captured image elsewhere (e.g. a profile picture)

```kotlin
CameraCaptureScreen(
    onImageCaptured = { uri ->
        profileViewModel.setProfileImage(uri)
    }
)

// Elsewhere, e.g. with Coil:
AsyncImage(
    model = imageUri,
    contentDescription = "Profile picture",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
)
```

## License

Add your license here (e.g. MIT, Apache 2.0).
