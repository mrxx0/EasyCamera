package com.mrxx0.easycamera.data.repository

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.hardware.camera2.CaptureRequest
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Range
import android.view.ScaleGestureDetector
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.domain.repository.EasyCameraRepository
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EasyCameraRepositoryImplementation @Inject constructor(
    private val cameraProvider: ProcessCameraProvider,
    private var cameraSelector: CameraSelector,
    private var cameraPreview: Preview,
    private var imageCapture: ImageCapture,
    private var videoCapture: VideoCapture<Recorder>,
    private var recording: Recording?,
    private val imageAnalysis: ImageAnalysis,
) : EasyCameraRepository {

    private var camControl : Camera? = null

    @SuppressLint("ClickableViewAccessibility")
    override suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        context: Context
    ) {
        cameraPreview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            cameraProvider.unbindAll()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            camControl = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
            )

            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val currentZoomRatio = camControl?.cameraInfo?.zoomState?.value?.zoomRatio ?: 0f
                    val delta = detector.scaleFactor

                    camControl?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                    return true
                }
            }
            val scaleGestureDetector = ScaleGestureDetector(context, listener)
            previewView.setOnTouchListener {
                _, event ->
                scaleGestureDetector.onTouchEvent(event)
                return@setOnTouchListener true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        cameraMode: Boolean
    ) {
        try {
            cameraProvider.unbindAll()
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            camControl = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                if (cameraMode) {
                    imageCapture
                } else {
                    videoCapture
                       },
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int
    ) {

        val imageName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        Executors.newSingleThreadScheduledExecutor().schedule({
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exception: ImageCaptureException) {
                        Log.e(
                            "EasyCamera",
                            "Photo capture failed: ${exception.message}",
                            exception
                        )
                    }

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val logMessage = "Photo capture succeeded: ${outputFileResults.savedUri}"
                        lastImageUri.value = outputFileResults.savedUri!!
                        Log.d("EasyCamera", logMessage)
                    }
                }
            )
        }, timerMode.toLong(), TimeUnit.SECONDS)
    }

    override suspend fun takeVideo(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int,
        viewModel: MainViewModel
    ) {
        val curRecording = recording
        if (curRecording != null) {
            viewModel.videoRecording = false
            curRecording.stop()
            recording = null
            return
        }
        val videoName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/Camera")
            }
        }
        val outputOptions = MediaStoreOutputOptions
            .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        if (!viewModel.videoRecording) {

            viewModel.videoRecording = true
            recording = videoCapture.output
                .prepareRecording(context, outputOptions)
                .apply {
                    if (PermissionChecker.checkSelfPermission(
                            context.applicationContext,
                            android.Manifest.permission.RECORD_AUDIO
                        ) ==
                        PermissionChecker.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            viewModel.videoRecording = true
                        }

                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                val logMessage =
                                    "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                                lastImageUri.value = recordEvent.outputResults.outputUri
                                Log.d("EasyCamera", logMessage)
                                viewModel.videoRecording = false
                            } else {
                                recording?.close()
                                recording = null
                                viewModel.videoRecording = false
                                Log.e(
                                    "EasyCamera",
                                    "Video capture failed ${recordEvent.error} + ${recordEvent.cause}"
                                )
                            }
                        }
                    }
                }
        } else {
            viewModel.videoRecording = false
            recording?.stop()
            recording = null
        }
    }

    override suspend fun setAspectRatio(
        lifecycleOwner: LifecycleOwner,
        aspectRatio: Int
    ) {
        try {
            cameraProvider.unbindAll()
            val newAspectRatioStrategy =
                AspectRatioStrategy(aspectRatio, AspectRatioStrategy.FALLBACK_RULE_AUTO)
            imageCapture = ImageCapture.Builder()
                .setResolutionSelector(
                    ResolutionSelector.Builder()
                        .setAspectRatioStrategy(newAspectRatioStrategy)
                        .build()
                )
                .build()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setImageFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Int
    ) {
        try {
            cameraProvider.unbindAll()
            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun imageMode(
        lifecycleOwner: LifecycleOwner
    ) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalCamera2Interop::class) override suspend fun videoMode(
        lifecycleOwner: LifecycleOwner
    ) {
        try {
            cameraProvider.unbindAll()
            camControl = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                videoCapture,
            )
            val newControl = Camera2CameraControl.from(camControl!!.cameraControl)
            newControl.captureRequestOptions = CaptureRequestOptions.Builder().setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                Range.create(30, 30)
            )
                .build()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setVideoResolution(
        lifecycleOwner: LifecycleOwner,
        videoQuality: Quality
    ) {
        val selector = QualitySelector
            .from(
                videoQuality,
                FallbackStrategy.higherQualityOrLowerThan(Quality.FHD)
            )
        val recorder = Recorder.Builder()
            .setExecutor(Executors.newSingleThreadExecutor())
            .setQualitySelector(selector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                videoCapture,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setVideoFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Boolean
    ) {
        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                videoCapture,
            )
            if (camera.cameraInfo.hasFlashUnit()) {
                camera.cameraControl.enableTorch(flashMode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalCamera2Interop::class) override suspend fun setVideoFPS(
        lifecycleOwner: LifecycleOwner,
        fpsValue: Int
    ) {
        try {
            cameraProvider.unbindAll()
            camControl = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                videoCapture,
            )
                val newControl = Camera2CameraControl.from(camControl!!.cameraControl)
                newControl.captureRequestOptions = CaptureRequestOptions.Builder().setCaptureRequestOption(
                    CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                    Range.create(30, fpsValue)
                ).build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}