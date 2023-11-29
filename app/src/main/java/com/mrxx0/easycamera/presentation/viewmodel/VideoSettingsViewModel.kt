package com.mrxx0.easycamera.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.hardware.camera2.CaptureRequest
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Range
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class VideoSettingsViewModel @Inject constructor(
    private var recording: Recording?
) : ViewModel() {

    private var fps = mutableIntStateOf(30)
    var fpsState = mutableStateOf(FpsState(sixty = false, thirty = true))

    private var quality = mutableStateOf(Quality.FHD)
    var qualityState = mutableStateOf(QualityState(ultraHd = false, fullHd = true, hd = false))

    private var flash = mutableStateOf(false)
    var flashState = mutableStateOf(FlashState(off = true, on = false))


    fun takeVideo(
        context: Context,
        lastImageUri: MutableState<Uri?>,
        viewModel: MainViewModel,
        videoCapture: VideoCapture<Recorder>,
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

    fun setVideoResolution(
        videoQuality: Quality
    ): VideoCapture<Recorder> {
        quality.value = videoQuality
        val selector = QualitySelector
            .from(
                quality.value,
                FallbackStrategy.higherQualityOrLowerThan(Quality.FHD)
            )
        val recorder = Recorder.Builder()
            .setExecutor(Executors.newSingleThreadExecutor())
            .setQualitySelector(selector)
            .build()
        return (VideoCapture.withOutput(recorder))
    }

    fun setFlash(
        cameraControl: Camera?,
        flashMode: Boolean
    ) {
        if (cameraControl?.cameraInfo?.hasFlashUnit() == true) {
            flash.value = flashMode
            cameraControl.cameraControl.enableTorch(flash.value)
        }
    }

    @OptIn(ExperimentalCamera2Interop::class)
    fun setFps(
        cameraControl: Camera?,
        fpsValue: Int
    ) {
        fps.intValue = fpsValue
        val newControl = Camera2CameraControl.from(cameraControl!!.cameraControl)
        newControl.captureRequestOptions = CaptureRequestOptions.Builder().setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
            Range.create(30, fps.intValue)
        ).build()
    }

    data class FpsState(
        var sixty: Boolean,
        var thirty: Boolean
    )

    data class QualityState(
        var ultraHd: Boolean,
        var fullHd: Boolean,
        var hd: Boolean
    )

    data class FlashState(
        var off: Boolean,
        var on: Boolean
    )
}
