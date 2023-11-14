package com.mrxx0.easycamera.domain.repository

import android.content.Context
import android.net.Uri
import androidx.camera.video.Quality
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel

interface EasyCameraRepository {
    suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        context: Context
    )
    suspend fun switchCamera(
        lifecycleOwner: LifecycleOwner,
        cameraMode: Boolean
    )
    suspend fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int
    )

    suspend fun takeVideo(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        viewModel: MainViewModel
    )

    suspend fun setAspectRatio(
        lifecycleOwner: LifecycleOwner,
        aspectRatio: Int
    )

    suspend fun setImageFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Int
    )

    suspend fun imageMode(
        lifecycleOwner: LifecycleOwner
    )
    suspend fun videoMode(
        lifecycleOwner: LifecycleOwner
    )
    suspend fun setVideoResolution(
        lifecycleOwner: LifecycleOwner,
        videoQuality: Quality
    )

    suspend fun setVideoFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Boolean
    )

    suspend fun setVideoFPS(
        fpsValue: Int
    )
}