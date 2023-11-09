package com.mrxx0.easycamera.presentation.view

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun MainView(
    viewModel: MainViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val screeHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CameraPreview(screenWidth, screeHeight, lifecycleOwner, viewModel)
        ControlZone(screeHeight, lifecycleOwner, context, viewModel)
    }
}

@Composable
fun CameraPreview(
    screenWidth: Dp,
    screeHeight: Dp,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel
) {
    var previewView: PreviewView
    var swapDirection by remember { mutableStateOf(-1)}
    var showCard by remember { mutableStateOf(false)}
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
                                showCard = false
                                Log.d("EasyCamera", "RIGHT gesture detected")
                            }

                            1 -> {
                                showCard = false
                                Log.d("EasyCamera", "LEFT gesture detected")

                            }

                            2 -> {
                                showCard = true
                                Log.d("EasyCamera", "DOWN gesture detected")

                            }

                            3 -> {
                                showCard = false
                                Log.d("EasyCamera", "UP gesture detected")
                            }
                        }
                    })
            }
            .height(screeHeight * 0.65f)
            .width(screenWidth)

    ) {
        AndroidView(
            factory = {
                previewView = PreviewView(it)
                viewModel.showCameraPreview(previewView, lifecycleOwner)
                previewView
            },
            modifier = Modifier
                .height(screeHeight * 0.65f)
                .width(screenWidth)
        )
        if (showCard) {
            SettingsCard(screeHeight, lifecycleOwner)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ControlZone(
    screeHeight: Dp,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    viewModel: MainViewModel
) {


    val lastImageUri = remember { mutableStateOf(viewModel.getLastImageUri(context)) }
    val cameraMode = remember { mutableStateOf(viewModel.getMode()) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {


        }
    ) { padding ->
        Box(
            modifier = Modifier
                .height(screeHeight * 0.35f)
                .fillMaxWidth()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .height(screeHeight * 0.35f)
                    .align(Alignment.BottomEnd)
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 50.dp, end = 50.dp, top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    PreviewLastTakenImage(lastImageUri, viewModel, context)
                    ShutterButton(context, lifecycleOwner, viewModel, lastImageUri, cameraMode.value)
                    OutlinedButton(
                        onClick = {
                            if (!viewModel.videoRecording) {
                                viewModel.switchCamera(lifecycleOwner, viewModel.cameraMode)
                            }
                        },
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, Color.White),
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "Switch camera",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row (
                        modifier = Modifier.background(Color.Black),
                        horizontalArrangement = Arrangement.Center,
                    ){
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Switch to image mode",
                            tint = Color.White,
                            modifier = Modifier
                                .size(54.dp)
                                .clickable {
                                    if (!viewModel.cameraMode) {
                                        viewModel.setMode(true)
                                        cameraMode.value = true
                                        viewModel.setImageMode(lifecycleOwner)
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = "Switch to video mode",
                            tint = Color.White,
                            modifier = Modifier
                                .size(54.dp)
                                .clickable {
                                    if (viewModel.cameraMode) {
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
    }
}

@Composable
fun ShutterButton(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel,
    lastImageUri: MutableState<Uri?>,
    cameraMode: Boolean
) {

    val shutterInteractionSource = remember { MutableInteractionSource() }
    val isShutterPressed by shutterInteractionSource.collectIsPressedAsState()
    val shutterButtonColor = if (isShutterPressed) {
        Color.DarkGray
    } else {
        if (cameraMode)
        {
            Color.White
        } else {
            Color.Red
        }
    }

    OutlinedButton(
        onClick = {
            if (cameraMode) {
                viewModel.takeImage(context, lastImageUri, viewModel.timerMode)
            } else {
                viewModel.takeVideo(context, lastImageUri, viewModel.timerMode, viewModel)
            }

        },
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        border = BorderStroke(4.dp, Color.White),
        contentPadding = PaddingValues(4.dp),
        interactionSource = shutterInteractionSource
    ) {
        Icon(
            Icons.Default.Circle,
            contentDescription = "Shutter button",
            tint = shutterButtonColor,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun PreviewLastTakenImage(lastImageUri: MutableState<Uri?>, viewModel: MainViewModel, context: Context) {
    OutlinedButton(
        onClick = { },
        modifier = Modifier.size(54.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.White),
        contentPadding = PaddingValues(1.dp),
    ) {
        if (lastImageUri.value != null) {
            val imageLoader = ImageLoader.Builder(context)
                .components{
                    add(VideoFrameDecoder.Factory())
                }.crossfade(true)
                .build()
            val painter = rememberAsyncImagePainter(
                model = lastImageUri.value!!,
                imageLoader = imageLoader)
            Image(
                painter = painter,
                contentDescription = "last taken image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.openGallery(lastImageUri, context)
                    }
            )
        } else {
            Icon(
                Icons.Default.HideImage,
                contentDescription = "Access gallery",
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}
