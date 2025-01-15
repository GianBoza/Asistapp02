package com.example.login_app.ui.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FaceComparinsonScreen(viewModel: FaceCaptureViewModel = viewModel()) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var captureState by remember { mutableStateOf("Esperando para capturar imagen") }
    var comparisonResult by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = captureState)

        Spacer(modifier = Modifier.height(16.dp))

        // Configuración de la cámara
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val previewView = remember { PreviewView(context) }
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()

            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            runCatching {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }.onFailure { exc ->
                Log.e("Camera", "Use case binding failed", exc)
            }
        }

        AndroidView(
            factory = { previewView },
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!isCapturing) {
                isCapturing = true
                captureState = "Capturando imagen en 3 segundos..."
                coroutineScope.launch {
                    delay(3000)
                    imageCapture?.let { capture ->
                        val executor = ContextCompat.getMainExecutor(context)
                        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = imageProxyToBitmap(image)
                                coroutineScope.launch {
                                    compareWithRegisteredFace(bitmap, context, viewModel) { result ->
                                        comparisonResult = result
                                        captureState = "Procesando comparación..."
                                    }
                                }
                                image.close()
                                isCapturing = false
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Camera", "Image capture failed", exception)
                                captureState = "Error al capturar imagen: ${exception.message}"
                                isCapturing = false
                            }
                        })
                    }
                }
            }
        }) {
            Text(text = "Capturar y Comparar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Imagen Capturada",
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        comparisonResult?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = if (it.contains("Rostros coinciden"))
                    Color.Green else Color.Red,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
private suspend fun compareWithRegisteredFace(
    newImage: Bitmap,
    context: Context,
    viewModel: FaceCaptureViewModel,
    onResult: (String) -> Unit
) {
    withContext(Dispatchers.Default) {
        try {
            val registeredImage = viewModel.getRegisteredFaceImage(context)
            if (registeredImage == null) {
                withContext(Dispatchers.Main) {
                    onResult("No se encontró imagen registrada previamente")
                }
                return@withContext
            }

            val faceNetModel = FaceNetModel.create(context)
            val similarity = faceNetModel.compareImages(registeredImage, newImage)

            withContext(Dispatchers.Main) {
                val resultMessage = if (similarity > FaceNetModel.SIMILARITY_THRESHOLD) {
                    """
                    ¡Rostros coinciden!
                    Usuario: RedDeadth
                    Fecha y Hora: 2025-01-14 19:16:00
                    Similaridad: ${String.format("%.2f", similarity)}
                    
                    Asistencia Registrada Exitosamente
                    """.trimIndent()
                } else {
                    """
                    Los rostros no coinciden
                    Similaridad: ${String.format("%.2f", similarity)}
                    
                    Por favor, intente nuevamente
                    """.trimIndent()
                }

                onResult(resultMessage)

                if (similarity > FaceNetModel.SIMILARITY_THRESHOLD) {
                    viewModel.registerAttendance(
                        "RedDeadth",
                        similarity,
                        "2025-01-14 19:16:00",
                        context
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FaceComparison", "Error durante la comparación", e)
            withContext(Dispatchers.Main) {
                onResult("""
                    Error en la comparación: ${e.message}
                    Fecha y Hora (UTC): 2025-01-14 19:16:00
                    Usuario: RedDeadth
                """.trimIndent())
            }
        }
    }
}

// Mantener la función imageProxyToBitmap como está
private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // Obtener la rotación de la imagen
    val rotation = image.imageInfo.rotationDegrees

    // Decodificar el array de bytes en un bitmap
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Crear una matriz para rotar la imagen si es necesario
    val matrix = Matrix()
    matrix.postRotate(rotation.toFloat())

    // Rotar el bitmap si es necesario y retornarlo
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}