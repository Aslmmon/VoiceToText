package com.idmt.simplevoice.ui.databse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier,
    onDismiss: () -> Unit,
    updateText: (String) -> Unit,
    comments: MutableList<String>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var text by remember { mutableStateOf("") }


    val trailingIconView = @Composable {
        IconButton(
            onClick = {
                updateText.invoke(text)
                text = ""
            },
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
        },
        sheetState = sheetState
    ) {
        LazyColumn(modifier = modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(comments) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(modifier = modifier, text = "Admin", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text(modifier = modifier, text = it, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(modifier = modifier, text = "Just Now", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                    Divider(modifier = modifier.fillMaxWidth().height(1.dp), color = Color.Gray)

                }
            }
        }

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 50.dp, horizontal = 10.dp),
            value = text,
            onValueChange = {
                text = it
            }, trailingIcon = trailingIconView
        )
    }

}