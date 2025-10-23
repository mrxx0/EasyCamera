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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.mrxx0.easycamera.presentation.viewmodel.ImageSettingsViewModel

@Composable
fun ImageSettings(
    lifecycleOwner: LifecycleOwner,
    setAspectRatio: (LifecycleOwner, Int) -> Unit,
    setFlashMode: (LifecycleOwner, Int) -> Unit,
    setTimerMode: (Int) -> Unit,
    ratioState: MutableState<ImageSettingsViewModel.RatioState>,
    flashState: MutableState<ImageSettingsViewModel.FlashState>,
    timerState: MutableState<ImageSettingsViewModel.TimerState>
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
                Text("Image", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
            }
            ImageRatioSettings(lifecycleOwner, setAspectRatio, ratioState)
            ImageFlashSettings(lifecycleOwner, setFlashMode, flashState)
            ImageTimerSettings(setTimerMode, timerState)
        }
    }
}

@Composable
fun ImageTimerSettings(
    setTimerMode: (Int) -> Unit,
    timerState: MutableState<ImageSettingsViewModel.TimerState>,
) {
    val currentTimer by remember {
        mutableStateOf(
            timerState.value
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, end = 10.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Timer", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))
            Row(
                horizontalArrangement = Arrangement.End,

                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // --- Off Button ---
                    val offColor = if (timerState.value.off) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            timerState.value = currentTimer.copy(off = true, three = false, ten = false)
                            setTimerMode(0)
                        }
                    ) {
                        Icon(
                            Icons.Default.TimerOff,
                            contentDescription = "Timer Off",
                            tint = offColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("Off", color = offColor, fontWeight = if (timerState.value.off) FontWeight.Bold else FontWeight.Normal)
                    }

                    // --- 3s Button ---
                    val threeColor = if (timerState.value.three) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            timerState.value = currentTimer.copy(off = false, three = true, ten = false)
                            setTimerMode(3)
                        }
                    ) {
                        Icon(
                            Icons.Default.Timer3Select,
                            contentDescription = "Timer 3s",
                            tint = threeColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("3s", color = threeColor, fontWeight = if (timerState.value.three) FontWeight.Bold else FontWeight.Normal)
                    }

                    // --- 10s Button ---
                    val tenColor = if (timerState.value.ten) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            timerState.value = currentTimer.copy(off = false, three = false, ten = true)
                            setTimerMode(10)
                        }
                    ) {
                        Icon(
                            Icons.Default.Timer10Select,
                            contentDescription = "Timer 10s",
                            tint = tenColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text("10s", color = tenColor, fontWeight = if (timerState.value.ten) FontWeight.Bold else FontWeight.Normal)
                    }
                }

            }
        }
    }
}

@Composable
fun ImageFlashSettings(
    lifecycleOwner: LifecycleOwner,
    setFlashMode: (LifecycleOwner, Int) -> Unit,
    flashState: MutableState<ImageSettingsViewModel.FlashState>,
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
                    // --- Flash Auto ---
                    val autoColor = if (flashState.value.flashAuto) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            flashState.value = currentFlash.copy(
                                flashAuto = true,
                                flashOn = false,
                                flashOff = false
                            )
                            setFlashMode(lifecycleOwner, ImageCapture.FLASH_MODE_AUTO)
                        }
                    ) {
                        Icon(
                            Icons.Default.FlashAuto,
                            contentDescription = "Flash Auto",
                            tint = autoColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "Auto",
                            color = autoColor,
                            fontWeight = if (flashState.value.flashAuto) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // --- Flash On ---
                    val onColor = if (flashState.value.flashOn) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            flashState.value = currentFlash.copy(
                                flashAuto = false,
                                flashOn = true,
                                flashOff = false
                            )
                            setFlashMode(lifecycleOwner, ImageCapture.FLASH_MODE_ON)
                        }
                    ) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = "Flash On",
                            tint = onColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "On",
                            color = onColor,
                            fontWeight = if (flashState.value.flashOn) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // --- Flash Off ---
                    val offColor = if (flashState.value.flashOff) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            flashState.value = currentFlash.copy(
                                flashAuto = false,
                                flashOn = false,
                                flashOff = true
                            )
                            setFlashMode(lifecycleOwner, ImageCapture.FLASH_MODE_OFF)
                        }
                    ) {
                        Icon(
                            Icons.Default.FlashOff,
                            contentDescription = "Flash Off",
                            tint = offColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "Off",
                            color = offColor,
                            fontWeight = if (flashState.value.flashOff) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ImageRatioSettings(
    lifecycleOwner: LifecycleOwner,
    setAspectRatio: (LifecycleOwner, Int) -> Unit,
    ratioState: MutableState<ImageSettingsViewModel.RatioState>,
) {
    val currentRatio by remember {
        mutableStateOf(
            ratioState.value
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
            Text(
                text = "Ratio",
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.4f))

            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // --- 3:4 Ratio ---
                    val threeByFourColor = if (ratioState.value.threeByFour) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            ratioState.value = currentRatio.copy(
                                threeByFour = true,
                                nineBySixteen = false
                            )
                            setAspectRatio(lifecycleOwner, AspectRatio.RATIO_4_3)
                        }
                    ) {
                        Icon(
                            Icons.Default.AspectRatio,
                            contentDescription = "Crop 3:4",
                            tint = threeByFourColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "3:4",
                            color = threeByFourColor,
                            fontWeight = if (ratioState.value.threeByFour) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // --- 9:16 Ratio ---
                    val nineBySixteenColor = if (ratioState.value.nineBySixteen) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            ratioState.value = currentRatio.copy(
                                threeByFour = false,
                                nineBySixteen = true
                            )
                            setAspectRatio(lifecycleOwner, AspectRatio.RATIO_16_9)
                        }
                    ) {
                        Icon(
                            Icons.Default.AspectRatio,
                            contentDescription = "Crop 9:16",
                            tint = nineBySixteenColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "9:16",
                            color = nineBySixteenColor,
                            fontWeight = if (ratioState.value.nineBySixteen) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

    }
}