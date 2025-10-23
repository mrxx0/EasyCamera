package com.mrxx0.easycamera.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel
import kotlin.math.abs

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainView(
    viewModel: MainViewModel = hiltViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val screeHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val showCard = mutableStateOf(false)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CameraPreview(screenWidth, screeHeight, lifecycleOwner, viewModel, showCard)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CameraPreview(
    screenWidth: Dp,
    screeHeight: Dp,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel,
    showCard: MutableState<Boolean>
) {
    var previewView: PreviewView
    var swapDirection by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onBackground),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {

        }
    ) { padding ->
        Box(
            modifier = Modifier
                .height(screeHeight)
                .fillMaxWidth()
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val (x, y) = dragAmount
                                if (abs(x) > abs(y)) {
                                    when {
                                        x > 0 -> {
                                            swapDirection = 0
                                        }

                                        x < 0 -> {
                                            swapDirection = 1
                                        }
                                    }
                                } else {
                                    when {
                                        y > 0 -> {
                                            swapDirection = 2
                                        }

                                        y < 0 -> {
                                            swapDirection = 3
                                        }
                                    }
                                }
                            },
                            onDragEnd = {
                                when (swapDirection) {
                                    0 -> {
                                        showCard.value = false
                                        Log.d("EasyCamera", "RIGHT gesture detected")
                                    }

                                    1 -> {
                                        showCard.value = false
                                        Log.d("EasyCamera", "LEFT gesture detected")

                                    }

                                    2 -> {
                                        showCard.value = true
                                        Log.d("EasyCamera", "DOWN gesture detected")

                                    }

                                    3 -> {
                                        showCard.value = false
                                        Log.d("EasyCamera", "UP gesture detected")
                                    }
                                }
                            })
                    }
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black)

            ) {
                AndroidView(
                    factory = {
                        previewView = PreviewView(it)
                        viewModel.showCameraPreview(previewView, lifecycleOwner, context)
                        previewView

                    },
                    modifier = Modifier
                        .height(
                            if (viewModel.fullscreen.value) {
                                screeHeight * 0.8f
                            } else {
                                screeHeight * 0.6f
                            }
                        )
                        .width(screenWidth)
                        .fillMaxHeight()
                        .background(Color.Black)
                )
                if (showCard.value && !viewModel.videoRecording) {
                    SettingsCard(lifecycleOwner)
                }
                ControlZone(screeHeight,
                    lifecycleOwner,
                    context,
                    viewModel,
                    showCard,
                    modifier = Modifier
                    .align(Alignment.BottomEnd))
            }
        }
    }


}

@Composable
fun ControlZone(
    screeHeight: Dp,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    viewModel: MainViewModel,
    showCard: MutableState<Boolean>,
    modifier: Modifier
) {
    val lastImageUri = remember { mutableStateOf(viewModel.getLastImageUri(context)) }
    val cameraMode = remember { mutableStateOf(viewModel.getMode()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        val modifierRow = if (viewModel.fullscreen.value) {
            Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(start = 70.dp, end = 70.dp, top = 32.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(start = 70.dp, end = 70.dp, top = 32.dp)
        }

        Row(
            modifier = modifierRow,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreviewLastTakenImage(lastImageUri, viewModel, context)
            ShutterButton(context, viewModel, lastImageUri, cameraMode.value)

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.85f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )

            OutlinedButton(
                onClick = {
                    if (!viewModel.videoRecording) {
                        viewModel.switchCamera(lifecycleOwner, viewModel.cameraMode)
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .scale(scale),
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(8.dp),
                interactionSource = interactionSource,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        val modifierColumn = if (viewModel.fullscreen.value) {
            Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        } else {
            Modifier
                .fillMaxWidth()
                .background(Color.Black)
        }

        ImageModeSwitch(
            modifier = modifierColumn,
            lifecycleOwner = lifecycleOwner,
            showCard = showCard,
            viewModel = viewModel,
            cameraMode = cameraMode,
            screenHeight = screeHeight
        )
    }
}

@Composable
fun ImageModeSwitch(
    modifier: Modifier,
    lifecycleOwner: LifecycleOwner,
    showCard: MutableState<Boolean>,
    viewModel: MainViewModel,
    cameraMode: MutableState<Boolean>,
    screenHeight: Dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = modifier.height(screenHeight * 0.2f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val photoInteractionSource = remember { MutableInteractionSource() }
                val photoPressed by photoInteractionSource.collectIsPressedAsState()
                val photoScale by animateFloatAsState(
                    targetValue = if (photoPressed) 0.8f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
                val photoColor by animateColorAsState(
                    targetValue = when {
                        showCard.value -> Color.Gray
                        viewModel.cameraMode -> Color.DarkGray
                        else -> Color.White
                    },
                    animationSpec = tween(300)
                )

                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = "Switch to image mode",
                    tint = photoColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(photoScale)
                        .clickable(
                            interactionSource = photoInteractionSource,
                            indication = rememberRipple(bounded = false, radius = 24.dp)
                        ) {
                            if (!showCard.value && !viewModel.cameraMode) {
                                viewModel.setMode(true)
                                cameraMode.value = true
                                viewModel.setImageMode(lifecycleOwner)
                            }
                        }
                )

                Spacer(modifier = Modifier.size(15.dp))

                val videoInteractionSource = remember { MutableInteractionSource() }
                val videoPressed by videoInteractionSource.collectIsPressedAsState()
                val videoScale by animateFloatAsState(
                    targetValue = if (videoPressed) 0.8f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
                val videoColor by animateColorAsState(
                    targetValue = when {
                        showCard.value -> Color.Gray
                        !viewModel.cameraMode -> Color.DarkGray
                        else -> Color.White
                    },
                    animationSpec = tween(300)
                )

                Icon(
                    Icons.Default.Videocam,
                    contentDescription = "Switch to video mode",
                    tint = videoColor,
                    modifier = Modifier
                        .size(30.dp)
                        .scale(videoScale)
                        .clickable(
                            interactionSource = videoInteractionSource,
                            indication = rememberRipple(bounded = false, radius = 24.dp)
                        ) {
                            if (!showCard.value && viewModel.cameraMode) {
                                viewModel.setMode(false)
                                cameraMode.value = false
                                viewModel.setVideoMode(lifecycleOwner)
                            }
                        }
                )
            }
        }
    }
}


@Composable
fun ShutterButton(
    context: Context,
    viewModel: MainViewModel,
    lastImageUri: MutableState<Uri?>,
    cameraMode: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val animatedColor by animateColorAsState(
        targetValue = if (cameraMode)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.error,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    )

    OutlinedButton(
        onClick = {},
        enabled = false,
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = Color.Transparent
        ),
        modifier = Modifier
            .size(80.dp)
            .scale(scale),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = animatedColor,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = 40.dp)
                ) {
                    if (cameraMode) {
                        viewModel.takeImage(context, lastImageUri, viewModel.mainTimer)
                    } else {
                        viewModel.takeVideo(context, lastImageUri, viewModel)
                    }
                }
        )
    }
}


@Composable
fun PreviewLastTakenImage(
    lastImageUri: MutableState<Uri?>,
    viewModel: MainViewModel,
    context: Context
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    OutlinedButton(
        onClick = {  },
        modifier = Modifier
            .size(50.dp)
            .scale(scale),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.White),
        contentPadding = PaddingValues(1.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (lastImageUri.value != null) {
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .crossfade(true)
                .build()
            val painter = rememberAsyncImagePainter(
                model = lastImageUri.value!!,
                imageLoader = imageLoader
            )
            Image(
                painter = painter,
                contentDescription = "Last taken image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(bounded = true, radius = 28.dp)
                    ) {
                        viewModel.openGallery(lastImageUri, context)
                    }
            )
        } else {
            Icon(
                Icons.Default.HideImage,
                contentDescription = "Access gallery",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
