package com.tchibo.plantbuddy.ui.components.addpage

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.tchibo.plantbuddy.utils.BarcodeAnalyser
import java.util.concurrent.Executors

@Composable
fun QrScanner(
    onQrCodeFound: (String) -> Unit = {}
) {

    Box(
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(1f)
            .clipToBounds()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                val cameraExecutor = Executors.newSingleThreadExecutor()

                val previewView = PreviewView(context).also {
                    it.scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                val cameraRunnable: Runnable = Runnable {
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val imageCapture = ImageCapture.Builder().build()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, BarcodeAnalyser { barcodeList ->
                                onQrCodeFound(barcodeList[0].displayValue.orEmpty())
                            })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                            context as ComponentActivity,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalyzer
                        )

                    } catch (exc: Exception) {
                        Log.e("DEBUG", "Use case binding failed", exc)
                    }
                }

                cameraProviderFuture.addListener(
                    cameraRunnable,
                    ContextCompat.getMainExecutor(context)
                )

                previewView // return
            }
        )
    }
}