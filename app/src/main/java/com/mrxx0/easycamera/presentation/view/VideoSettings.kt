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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.VideoSettingsViewModel

@Composable
fun VideoSettings(
    lifecycleOwner: LifecycleOwner,
    setVideoFlash: (LifecycleOwner, Boolean) -> Unit,
    setFpsValue: (Int) -> Unit,
    setVideoQuality: (LifecycleOwner, Quality) -> Unit,
    fpsState: MutableState<VideoSettingsViewModel.FpsState>,
    qualityState: MutableState<VideoSettingsViewModel.QualityState>,
    flashState: MutableState<VideoSettingsViewModel.FlashState>,
) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .padding(top = 50.dp)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background),
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
                Text("Video", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
            }
            VideoFpsSettings(setFpsValue, fpsState)
            VideoQualitySettings(lifecycleOwner, setVideoQuality, qualityState)
            VideoFlashSettings(lifecycleOwner, setVideoFlash, flashState)
        }
    }
}

@Composable
fun VideoFlashSettings(
    lifecycleOwner: LifecycleOwner,
    setVideoFlash: (LifecycleOwner, Boolean) -> Unit,
    flashState: MutableState<VideoSettingsViewModel.FlashState>
) {
    val currentFlash by remember {
        mutableStateOf(
            flashState.value
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Flash", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val onColor = if (flashState.value.on)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            flashState.value = currentFlash.copy(off = false, on = true)
                            setVideoFlash(
                                lifecycleOwner,
                                true
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = "Flash On",
                            tint = onColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("On", color = onColor,
                            fontWeight = if (flashState.value.on) FontWeight.Bold else FontWeight.Normal)
                    }
                    val offColor = if (flashState.value.off)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            flashState.value = currentFlash.copy(off = true, on = false)
                            setVideoFlash(lifecycleOwner, false)
                        }
                    ) {
                        Icon(
                            Icons.Default.FlashOff,
                            contentDescription = "Flash Off",
                            tint = offColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("Off", color = offColor,
                            fontWeight = if (flashState.value.off) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoFpsSettings(
    setFpsValue: (Int) -> Unit,
    fpsState: MutableState<VideoSettingsViewModel.FpsState>
) {

    val currentFps by remember {
        mutableStateOf(
            fpsState.value
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Frame/sec", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val sixtyColor = if (fpsState.value.sixty)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            fpsState.value = currentFps.copy(sixty = true, thirty = false)
                            setFpsValue(
                                60
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.AutoAwesomeMotion,
                            contentDescription = "60fps",
                            tint = sixtyColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("60 fps", color = sixtyColor,
                            fontWeight = if (fpsState.value.sixty) FontWeight.Bold else FontWeight.Normal)
                    }
                    val thirtyColor = if (fpsState.value.thirty)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            fpsState.value = currentFps.copy(sixty = false, thirty = true)
                            setFpsValue(
                                30
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.AutoAwesomeMotion,
                            contentDescription = "30fps",
                            tint = thirtyColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("30 fps", color = thirtyColor,
                            fontWeight = if (fpsState.value.thirty) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}


@Composable
fun VideoQualitySettings(
    lifecycleOwner: LifecycleOwner,
    setVideoQuality: (LifecycleOwner, Quality) -> Unit,
    qualityState: MutableState<VideoSettingsViewModel.QualityState>
) {
    val currentQuality by remember {
        mutableStateOf(
            qualityState.value
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Quality", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val ultraColor = if (qualityState.value.ultraHd)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            qualityState.value =
                                currentQuality.copy(ultraHd = true, fullHd = false, hd = false)
                            setVideoQuality(lifecycleOwner, Quality.UHD)
                        }
                    ) {
                        Icon(
                            Icons.Default.Hd,
                            contentDescription = "4k",
                            tint = ultraColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("4K", color = ultraColor,
                            fontWeight = if (qualityState.value.ultraHd) FontWeight.Bold else FontWeight.Normal)
                    }
                    val fullColor = if (qualityState.value.fullHd)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            qualityState.value =
                                currentQuality.copy(ultraHd = false, fullHd = true, hd = false)
                            setVideoQuality(lifecycleOwner, Quality.FHD)
                        }
                    ) {
                        Icon(
                            Icons.Default.Hd,
                            contentDescription = "1080p",
                            tint = fullColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("1080p", color = fullColor,
                            fontWeight = if (qualityState.value.fullHd) FontWeight.Bold else FontWeight.Normal)
                    }

                    val hdColor = if (qualityState.value.hd)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            qualityState.value =
                                currentQuality.copy(ultraHd = false, fullHd = false, hd = true)
                            setVideoQuality(lifecycleOwner, Quality.HD)
                        }
                    ) {
                        Icon(
                            Icons.Default.Hd,
                            contentDescription = "720p",
                            tint = hdColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("720p", color = hdColor,
                            fontWeight = if (qualityState.value.hd) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}
