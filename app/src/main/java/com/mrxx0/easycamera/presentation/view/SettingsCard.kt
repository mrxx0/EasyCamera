package com.mrxx0.easycamera.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel

@Composable
fun SettingsCard(
    screeHeight: Dp,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel = hiltViewModel()

) {
    if (viewModel.getMode()) {
        ImageSettings(lifecycleOwner)
    } else {
        VideoSettings(lifecycleOwner)
    }
}