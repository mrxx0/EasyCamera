package com.mrxx0.easycamera

import android.content.Context
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EasyCameraScreen(
    cameraX: CameraX,
) {

    var lensId by remember {
        mutableStateOf(cameraX.cameraSelector)
    }
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {

        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AppPreview(cameraX)
            Column(
                modifier = Modifier
                    .height(200.dp)
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
                    OutlinedButton(
                        onClick = { },
                        modifier= Modifier.size(54.dp),
                        shape = CircleShape,
                        border= BorderStroke(1.dp, Color.White),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.White)
                    ) {
                        Icon(Icons.Default.HideImage, contentDescription = "Access gallery", tint = Color.White, modifier = Modifier.size(54.dp))
                    }
                    OutlinedButton(
                        onClick = {
                                  cameraX.captureImage()
                        },
                        modifier= Modifier.size(80.dp),
                        shape = CircleShape,
                        border= BorderStroke(4.dp, Color.White),
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.White)
                    ) {
                        Icon(Icons.Default.Brightness1, contentDescription = "Shutter button", tint = Color.White, modifier = Modifier.size(80.dp))
                    }
                    OutlinedButton(
                        onClick = {
                            cameraX.switchLens()
                            lensId = cameraX.cameraSelector
                            Log.i("EasyCamera", "Switching camera")
                        },
                        modifier= Modifier.size(54.dp),
                        shape = CircleShape,
                        border= BorderStroke(1.dp, Color.White),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.White)
                    ) {
                        Icon(Icons.Default.Cached, contentDescription = "Swap camera", tint = Color.White, modifier = Modifier.size(54.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AppPreview(
    cameraX: CameraX
) {
    AndroidView(factory = { cameraX.previewViewTest }, modifier = Modifier.fillMaxSize())
}
