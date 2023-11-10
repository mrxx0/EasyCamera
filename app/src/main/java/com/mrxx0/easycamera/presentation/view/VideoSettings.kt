package com.mrxx0.easycamera.presentation.view

import androidx.camera.video.Quality
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
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.twotone.AutoAwesomeMotion
import androidx.compose.material.icons.twotone.Hd
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
fun VideoSettings(
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
                Text("Video", color = Color.White, fontSize = 20.sp)
            }
            VideoFpsSettings(lifecycleOwner, viewModel)
            VideoQualitySettings(lifecycleOwner, viewModel)
            VideoFlashSettings(lifecycleOwner, viewModel)
        }
    }
}

@Composable
fun VideoFlashSettings(
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
                            Icons.Default.FlashOn,
                            contentDescription = "Flash On",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setVideoFlash(
                                        lifecycleOwner,
                                        true
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
                                    viewModel.setVideoFlash(
                                        lifecycleOwner,
                                        false
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
fun VideoFpsSettings(
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
            Text("Frame/sec", color = MaterialTheme.colors.onPrimary)
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
                            Icons.TwoTone.AutoAwesomeMotion,
                            contentDescription = "60fps",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setFpsValue(
                                        lifecycleOwner,
                                        60
                                    )
                                }
                        )
                        Text("60 fps", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.TwoTone.AutoAwesomeMotion,
                            contentDescription = "30fps",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setFpsValue(
                                        lifecycleOwner,
                                        30
                                    )
                                }
                        )
                        Text("30 fps", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun VideoQualitySettings(
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
            Text("Quality", color = MaterialTheme.colors.onPrimary)
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
                            Icons.TwoTone.Hd,
                            contentDescription = "4k",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setVideoQuality(lifecycleOwner, Quality.UHD)
                                }
                        )
                        Text("4K", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.TwoTone.Hd,
                            contentDescription = "1080p",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setVideoQuality(lifecycleOwner, Quality.FHD)
                                }
                        )
                        Text("1080p", color = Color.White)
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.TwoTone.Hd,
                            contentDescription = "720p",
                            tint = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.setVideoQuality(lifecycleOwner, Quality.HD)
                                }
                        )
                        Text("720p", color = Color.White)
                    }
                }
            }
        }
    }
}
