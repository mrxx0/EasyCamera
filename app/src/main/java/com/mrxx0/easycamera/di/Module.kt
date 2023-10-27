package com.mrxx0.easycamera.di

import android.app.Application
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.mrxx0.easycamera.data.repository.EasyCameraRepositoryImplementation
import com.mrxx0.easycamera.domain.repository.EasyCameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    /*TODO*/
//    @OptIn(ExperimentalCamera2Interop::class)
//    @Provides
//    @Singleton
//    fun provideCameraSelector(provider: CameraProvider):CameraSelector?{
//        val cam2Infos = provider.availableCameraInfos.map {
//            Camera2CameraInfo.from(it)
//        }.sortedByDescending {
//            // HARDWARE_LEVEL is Int type, with the order of:
//            // LEGACY < LIMITED < FULL < LEVEL_3 < EXTERNAL
//            it.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
//        }
//
//        return when {
//            cam2Infos.isNotEmpty() -> {
//                CameraSelector.Builder()
//                    .addCameraFilter {
//                        it.filter { camInfo ->
//                            // cam2Infos[0] is either EXTERNAL or best built-in camera
//                            val thisCamId = Camera2CameraInfo.from(camInfo).cameraId
//                            thisCamId == cam2Infos[0].cameraId
//                        }
//                    }.build()
//            }
//            else -> null
//        }
//    }

    @Provides
    @Singleton
    fun provideCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    @Provides
    @Singleton
    fun provideCameraProvider(application: Application):ProcessCameraProvider{
        return ProcessCameraProvider.getInstance(application).get()
    }

    @Provides
    @Singleton
    fun provideCameraPreview():Preview{
        return Preview.Builder().build()
    }

    @Provides
    @Singleton
    fun provideImageCapture():ImageCapture{
        return ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageAnalysis():ImageAnalysis{
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    @Provides
    @Singleton
    fun provideEasyCameraRepository(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        cameraPreview: Preview,
        imageCapture: ImageCapture,
        imageAnalysis: ImageAnalysis,
    ):EasyCameraRepository {
        return EasyCameraRepositoryImplementation(
            cameraProvider,
            cameraSelector,
            cameraPreview,
            imageCapture,
            imageAnalysis
        )
    }

}