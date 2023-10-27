package com.mrxx0.easycamera.presentation.viewmodel

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrxx0.easycamera.domain.repository.EasyCameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cameraRepository: EasyCameraRepository
):ViewModel() {

    fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            cameraRepository.showCameraPreview(
                previewView,
                lifecycleOwner
                )
        }
    }
    fun switchCamera(
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            cameraRepository.switchCamera(
                lifecycleOwner
            )
        }
    }
    fun takeImage(
        context: Context,
    ) {
        viewModelScope.launch{
            cameraRepository.takeImage(
                context,
            )
        }
    }
}