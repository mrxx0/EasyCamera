package com.mrxx0.easycamera.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.video.Quality
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
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

    var cameraMode = true
    var timerMode = 0
    var videoRecording = false

    fun getMode() : Boolean {
        return cameraMode
    }
    fun setMode(mode : Boolean) {
        cameraMode = mode
    }

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
        lifecycleOwner: LifecycleOwner,
        cameraMode: Boolean
    ) {
        viewModelScope.launch {
            cameraRepository.switchCamera(
                lifecycleOwner,
                cameraMode
            )
        }
    }
    fun takeImage(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int
    ) {
        viewModelScope.launch{
            cameraRepository.takeImage(
                context,
                lastImageUri,
                timerMode
            )
        }
    }

    fun takeVideo(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        timerMode: Int,
        viewModel: MainViewModel
    ) {
        viewModelScope.launch {
            cameraRepository.takeVideo(
                context,
                lastImageUri,
                timerMode,
                viewModel
            )
        }
    }

    @SuppressLint("Range")
    fun getLastImageUri(context: Context): Uri?{
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val imageId = it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID))
                cursor.close()
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)
            }
        }
        cursor?.close()
        return null
    }

    fun openGallery(lastImageUri: MutableState<Uri?>, context: Context) {
        if (lastImageUri.value != null) {
            val galleryIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(lastImageUri.value!!, "image/*")
            }
            try {
                context.startActivity(galleryIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No application found to open image", Toast.LENGTH_SHORT).show()
                Log.d("EasyCamera", "No application found to open image + $e")
            }
        }
    }

    fun setAspectRatio(
        lifecycleOwner: LifecycleOwner,
        aspectRatio: Int
    ) {
        viewModelScope.launch {
            cameraRepository.setAspectRatio(
                lifecycleOwner,
                aspectRatio
            )
        }
    }

    fun setFlashMode(
        lifecycleOwner: LifecycleOwner,
        flashMode: Int
    ) {
        viewModelScope.launch {
            cameraRepository.setImageFlashMode(
                lifecycleOwner,
                flashMode
            )
        }
    }

    fun setImageMode(
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            cameraRepository.imageMode(lifecycleOwner)
        }
    }

    fun setVideoMode(
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            cameraRepository.videoMode(lifecycleOwner)
        }
    }

    fun setVideoQuality(
        lifecycleOwner: LifecycleOwner,
        videoQuality: Quality
    ) {
        viewModelScope.launch {
            cameraRepository.setVideoResolution(
                lifecycleOwner,
                videoQuality
            )
        }
    }

    fun setVideoFlash(
        lifecycleOwner: LifecycleOwner,
        flashMode: Boolean
    ) {
        viewModelScope.launch {
            cameraRepository.setVideoFlashMode(
                lifecycleOwner,
                flashMode
            )
        }
    }

    fun setFpsValue(
        lifecycleOwner: LifecycleOwner,
        fpsValue: Int
    ) {
        viewModelScope.launch {
            cameraRepository.setVideoFPS(
                lifecycleOwner,
                fpsValue
            )
        }
    }
}