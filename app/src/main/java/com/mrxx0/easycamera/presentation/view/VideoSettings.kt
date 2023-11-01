package com.mrxx0.easycamera.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop54
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VideoSettings(screeHeight: Dp) {
    Box(modifier = Modifier
        .padding(28.dp)
        .clip(shape = RoundedCornerShape(15.dp))
        .background(Color.DarkGray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Text("Video", color = Color.White)
        }
        Column(
            modifier = Modifier
                .height(screeHeight / 4),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Divider(thickness = 1.dp, color = Color.White)
            VideoRatioSettings()
        }
    }
}



@Composable
fun VideoRatioSettings() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Ratio", color = MaterialTheme.colors.onPrimary)
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Icon(
                    Icons.Default.Crop54,
                    contentDescription = "Crop 4:3",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
                Icon(
                    Icons.Default.CropPortrait,
                    contentDescription = "Crop 16:9",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
                Icon(
                    Icons.Default.CropSquare,
                    contentDescription = "Crop 1:1",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
        }
    }
}
