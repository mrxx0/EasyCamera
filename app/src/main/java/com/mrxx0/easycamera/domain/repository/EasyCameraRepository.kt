package com.mrxx0.easycamera.domain.repository

import android.content.Context
import androidx.camera.view.PreviewView
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
        context: Context
    )
}