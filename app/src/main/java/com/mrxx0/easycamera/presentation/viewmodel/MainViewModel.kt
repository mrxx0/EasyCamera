package com.mrxx0.easycamera.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
        lastImageUri: MutableState<Uri?>,
    ) {
        viewModelScope.launch{
            cameraRepository.takeImage(
                context,
                lastImageUri
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
}