package com.amyartist.illusionaireweb.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(key1 = cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {  },
        bottomBar = {
            if (cameraPermissionState.status.isGranted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        cameraController.cameraSelector =
                            if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    }) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        takePhoto(context, cameraController) { bitmap ->
                            capturedBitmap = bitmap
                            onImageCaptured(bitmap)
                        }
                    }) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Take Photo",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraPermissionState.status.isGranted) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            this.controller = cameraController
                            cameraController.bindToLifecycle(lifecycleOwner)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission is required to use this feature.")
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                    // Flip horizontally for front camera if needed (check image.imageInfo.cameraFacing)
                     if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                         postScale(-1f, 1f, image.width / 2f, image.height / 2f)
                     }
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                onPhotoTaken(rotatedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraScreen", "Error taking photo: ${exception.message}", exception)
            }
        }
    )
}
