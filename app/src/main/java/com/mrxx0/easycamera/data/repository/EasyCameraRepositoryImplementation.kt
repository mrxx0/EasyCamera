package com.mrxx0.easycamera.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CaptureRequest
import android.net.Uri
import android.util.Range
import android.view.ScaleGestureDetector
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.domain.repository.EasyCameraRepository
import com.mrxx0.easycamera.presentation.viewmodel.ImageSettingsViewModel
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel
import com.mrxx0.easycamera.presentation.viewmodel.VideoSettingsViewModel
import javax.inject.Inject

class EasyCameraRepositoryImplementation @Inject constructor(
    private val cameraProvider: ProcessCameraProvider,
    private var cameraSelector: CameraSelector,
    private var cameraPreview: Preview,
    private var imageCapture: ImageCapture,
    private var videoCapture: VideoCapture<Recorder>,
    recording: Recording?,
    private val imageAnalysis: ImageAnalysis,
) : EasyCameraRepository {

    private var camControl: Camera? = null
    private val imageCamera = ImageSettingsViewModel()
    private val videoCamera = VideoSettingsViewModel(recording)

    @SuppressLint("ClickableViewAccessibility")
    override suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        context: Context
    ) {
        cameraPreview.setSurfaceProvider(previewView.surfaceProvider)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        updateImageCameraUseCase(lifecycleOwner)
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camControl?.cameraInfo?.zoomState?.value?.zoomRatio ?: 0f
                val delta = detector.scaleFactor

                camControl?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(context, listener)
        previewView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    override suspend fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        cameraMode: Boolean
    ) {
        cameraSelector =
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        if (cameraMode) {
            updateImageCameraUseCase(lifecycleOwner)
        } else {
            updateVideoCameraUseCase(lifecycleOwner)
        }
    }

    override suspend fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int
    ) {
        try {
            imageCamera.takePicture(
                context, lastImageUri, imageCapture, timerMode
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun takeVideo(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        viewModel: MainViewModel
    ) {
        try {
            videoCamera.takeVideo(
                context, lastImageUri, viewModel, videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setAspectRatio(
        lifecycleOwner: LifecycleOwner,
        aspectRatio: Int,
        fullscreen: MutableState<Boolean>
    ) {
        imageCapture = imageCamera.setAspectRatio(aspectRatio, fullscreen)
        updateImageCameraUseCase(lifecycleOwner)

    }

    override suspend fun setImageFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Int
    ) {
        imageCapture = imageCamera.setFlashMode(flashMode)
        updateImageCameraUseCase(lifecycleOwner)
    }

    override suspend fun imageMode(
        lifecycleOwner: LifecycleOwner,
        fullscreen: MutableState<Boolean>
    ) {
        fullscreen.value = imageCamera.getRatio() == AspectRatio.RATIO_16_9
        updateImageCameraUseCase(lifecycleOwner)
    }

    @OptIn(ExperimentalCamera2Interop::class)
    override suspend fun videoMode(
        lifecycleOwner: LifecycleOwner
    ) {
        updateVideoCameraUseCase(lifecycleOwner)
        val newControl = Camera2CameraControl.from(camControl!!.cameraControl)
        newControl.captureRequestOptions = CaptureRequestOptions.Builder().setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
            Range.create(30, 30)
        )
            .build()
    }

    override suspend fun setVideoResolution(
        lifecycleOwner: LifecycleOwner,
        videoQuality: Quality
    ) {
        videoCapture = videoCamera.setVideoResolution(videoQuality)
        updateVideoCameraUseCase(lifecycleOwner)
    }

    override suspend fun setVideoFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Boolean
    ) {
        videoCamera.setFlash(camControl, flashMode)
    }

    @OptIn(ExperimentalCamera2Interop::class)
    override suspend fun setVideoFPS(
        fpsValue: Int
    ) {
        videoCamera.setFps(camControl, fpsValue)
    }

    private fun updateImageCameraUseCase(
        lifecycleOwner: LifecycleOwner
    ) {
        try {
            cameraProvider.unbindAll()
            camControl = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreview,
                imageCapture,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateVideoCameraUseCase(
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}