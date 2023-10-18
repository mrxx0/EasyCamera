package com.mrxx0.easycamera

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.ExperimentalVideo
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mrxx0.easycamera.ui.theme.EasyCameraTheme

@ExperimentalVideo class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERA_PERMISSIONS, 0
            )
        }
        setContent {
            EasyCameraTheme {
                val scaffoldState = rememberBottomSheetScaffoldState()
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
                        )
                    }
                }
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
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier.fillMaxSize()
                        )
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
                                    onClick = { },
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
                                        controller.cameraSelector =
                                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                                CameraSelector.DEFAULT_FRONT_CAMERA
                                            } else
                                                CameraSelector.DEFAULT_BACK_CAMERA
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
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERA_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERA_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    }
}
