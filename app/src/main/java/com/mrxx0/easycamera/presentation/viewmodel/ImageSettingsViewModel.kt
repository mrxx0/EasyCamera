package com.mrxx0.easycamera.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ImageSettingsViewModel @Inject constructor() : ViewModel() {

    private var flash = mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO)
    var flashState = mutableStateOf(FlashState(flashAuto = true, flashOff = false, flashOn = false))

    private var ratio = mutableIntStateOf(AspectRatio.RATIO_4_3)
    var ratioState = mutableStateOf(RatioState(threeByFour = true, nineBySixteen = false))

    var timerState = mutableStateOf(TimerState(off = true, three = false, ten = false))

    fun takePicture(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        imageCapture: ImageCapture,
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

    fun setAspectRatio(
        aspectRatio: Int,
    ): ImageCapture {

        ratio.intValue = aspectRatio
        val newAspectRatioStrategy =
            AspectRatioStrategy(aspectRatio, AspectRatioStrategy.FALLBACK_RULE_AUTO)
        return ImageCapture.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(newAspectRatioStrategy)
                    .build()
            )
            .setFlashMode(flash.intValue)
            .build()
    }

    fun setFlashMode(
        flashMode: Int,
    ): ImageCapture {

        flash.intValue = flashMode
        val currentRatio = ratio.intValue
        return ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy(
                            currentRatio,
                            AspectRatioStrategy.FALLBACK_RULE_AUTO
                        )
                    )
                    .build()
            )
            .build()
    }

    data class RatioState(
        var threeByFour: Boolean ,
        var nineBySixteen: Boolean
    )
    data class FlashState (
        var flashAuto: Boolean,
        var flashOn : Boolean,
        var flashOff : Boolean
    )
    data class TimerState(
        var off: Boolean,
        var three: Boolean,
        var ten: Boolean
    )
}