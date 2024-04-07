package com.idmt.simplevoice.ui.databse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShowLoadingView(modifier: Modifier) {
    Column(
        modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .background(
                    Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(25.dp)

                ), contentAlignment = Alignment.Center
        ) {
            Column {
                CircularProgressIndicator(color = Color.Black, strokeWidth = 1.dp)
                Spacer(modifier = modifier.height(10.dp))
                Text(text = "loading..")
            }
        }
    }
}