package com.mrxx0.easycamera.presentation.view

import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Timer10Select
import androidx.compose.material.icons.filled.Timer3Select
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel

@Composable
fun ImageSettings(
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel = hiltViewModel()
) {
    Box(modifier = Modifier
        .padding(12.dp)
        .padding(top = 50.dp)
        .clip(shape = RoundedCornerShape(15.dp))
        .background(Color.DarkGray),
    ) {

        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Image", color = Color.White, fontSize = 20.sp)
            }
            ImageRatioSettings(lifecycleOwner, viewModel)
            ImageFlashSettings(lifecycleOwner, viewModel)
            ImageTimerSettings(viewModel)
        }
    }
}

@Composable
fun ImageTimerSettings(
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Timer", color = MaterialTheme.colors.onPrimary)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))
            Row (horizontalArrangement = Arrangement.End,

            ){
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.TimerOff,
                            contentDescription = "Timer Off",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.timerMode = 0
                                }
                        )
                        Text("Off", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Timer3Select,
                            contentDescription = "Timer 3s",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.timerMode = 3
                                }
                        )
                        Text("3s", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Timer10Select,
                            contentDescription = "Timer 10s",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.timerMode = 10
                                }
                        )
                        Text("10s", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageFlashSettings(
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Flash", color = MaterialTheme.colors.onPrimary)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FlashAuto,
                            contentDescription = "Flash Auto",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setFlashMode(
                                        lifecycleOwner,
                                        ImageCapture.FLASH_MODE_AUTO
                                    )
                                }
                        )
                        Text("Auto", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = "Flash On",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setFlashMode(
                                        lifecycleOwner,
                                        ImageCapture.FLASH_MODE_ON
                                    )
                                }
                        )
                        Text("On", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FlashOff,
                            contentDescription = "Flash Off",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setFlashMode(
                                        lifecycleOwner,
                                        ImageCapture.FLASH_MODE_OFF
                                    )
                                }
                        )
                        Text("Off", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageRatioSettings(
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Ratio", color = MaterialTheme.colors.onPrimary)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row (
                horizontalArrangement = Arrangement.End
            ){
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AspectRatio,
                            contentDescription = "Crop 3:4",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setAspectRatio(
                                        lifecycleOwner,
                                        AspectRatio.RATIO_4_3
                                    )
                                }
                        )
                        Text("3:4", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AspectRatio,
                            contentDescription = "Crop 9:16",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setAspectRatio(
                                        lifecycleOwner,
                                        AspectRatio.RATIO_16_9
                                    )
                                }
                        )
                        Text("9:16", color = Color.White)
                    }
                }
            }
        }
    }
}
