package com.mrxx0.easycamera.presentation.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.ImageSettingsViewModel
import com.mrxx0.easycamera.presentation.viewmodel.VideoSettingsViewModel
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel

@Composable
fun SettingsCard(
    lifecycleOwner: LifecycleOwner,
    mainViewModel: MainViewModel = hiltViewModel(),
    videoSettingsViewModel: VideoSettingsViewModel = hiltViewModel(),
    imageSettingsViewModel: ImageSettingsViewModel = hiltViewModel()
) {
    if (mainViewModel.getMode()) {
        ImageSettings(
            lifecycleOwner,
            mainViewModel::setAspectRatio,
            mainViewModel::setFlashMode,
            mainViewModel::setTimerMode,
            imageSettingsViewModel.ratioState,
            imageSettingsViewModel.flashState,
            imageSettingsViewModel.timerState
        )
    } else {
        VideoSettings(
            lifecycleOwner,
            mainViewModel::setVideoFlash,
            mainViewModel::setFpsValue,
            mainViewModel::setVideoQuality,
            videoSettingsViewModel.fpsState,
            videoSettingsViewModel.qualityState,
            videoSettingsViewModel.flashState
        )
    }
}