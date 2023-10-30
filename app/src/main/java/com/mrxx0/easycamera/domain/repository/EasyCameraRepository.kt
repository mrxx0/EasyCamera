package com.mrxx0.easycamera.domain.repository

import android.content.Context
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner

interface EasyCameraRepository {
    suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    )
    suspend fun switchCamera(
        lifecycleOwner: LifecycleOwner
    )
    suspend fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
    )
}