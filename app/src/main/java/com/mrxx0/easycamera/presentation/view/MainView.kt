package com.mrxx0.easycamera.presentation.view

import android.content.Context
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.mrxx0.easycamera.presentation.viewmodel.MainViewModel

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

    Box(modifier = Modifier
        .height(screeHeight * 0.65f)
        .width(screenWidth)) {
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

    val shutterInteractionSource = remember { MutableInteractionSource() }
    val isShutterPressed by shutterInteractionSource.collectIsPressedAsState()
    val shutterButtonColor = if (isShutterPressed) Color.DarkGray else Color.White
    val lastImageUri = remember { mutableStateOf(viewModel.getLastImageUri(context)) }

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
                    .background(Color.Black)
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
                    OutlinedButton(
                        onClick = {
                            viewModel.takeImage(context, lastImageUri)

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
                    OutlinedButton(
                        onClick = {
                                  viewModel.switchCamera(lifecycleOwner)
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
            }
        }
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
            Image(
                painter = rememberAsyncImagePainter(lastImageUri.value!!),
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
