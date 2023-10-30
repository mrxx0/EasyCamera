package com.mrxx0.easycamera.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.domain.repository.EasyCameraRepository
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class EasyCameraRepositoryImplementation @Inject constructor(
    private val cameraProvider: ProcessCameraProvider,
    private var cameraSelector: CameraSelector,
    private val cameraPreview: Preview,
    private val imageCapture: ImageCapture,
    private val imageAnalysis: ImageAnalysis,
):EasyCameraRepository {
    
    override suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        cameraPreview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            cameraProvider.unbindAll()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun switchCamera(
        lifecycleOwner: LifecycleOwner
    ) {
        try {
            cameraProvider.unbindAll()
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
    ) {
        val imageName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("EasyCamera","Photo capture failed: ${exception.message}", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val logMessage = "Photo capture succeeded: ${outputFileResults.savedUri}"
                    lastImageUri.value = outputFileResults.savedUri!!
                    Log.d("EasyCamera", logMessage)
                }
            }
        )
    }
}