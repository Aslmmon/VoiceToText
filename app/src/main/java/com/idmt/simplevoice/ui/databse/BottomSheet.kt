package com.idmt.simplevoice.ui.databse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
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
import com.idmt.simplevoice.ui.databse.components.Loader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier,
    onDismiss: () -> Unit,
    refreshList: () -> Unit,
    onLaunchBottomSheet: () -> Unit,
    commentState: DataBaseViewModel.CommentsUiState?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var updateTextClicked by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = 1
    )


    LaunchedEffect(Unit) {
        onLaunchBottomSheet.invoke()
    }

    val trailingIconView = @Composable {
        IconButton(
            onClick = {
                updateTextClicked = true
                // text = ""
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
        when (commentState) {
            is DataBaseViewModel.CommentsUiState.Loading -> {
                Loader(modifier = modifier)
            }

            is DataBaseViewModel.CommentsUiState.Error -> {}
            is DataBaseViewModel.CommentsUiState.SubmitSuccess -> {
                //  refreshList.invoke()
            }

            is DataBaseViewModel.CommentsUiState.Success -> {

                Column(
                    modifier = modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1.2f),
                        state = listState,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        val data = commentState.data
                        items(data!!) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    modifier = modifier,
                                    text = "Admin",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    modifier = modifier,
                                    text = it.comments,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = modifier,
                                    text = "Just Now",
                                    fontSize = 12.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Divider(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .height(1.dp), color = Color.Gray
                                )

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

            else -> {}

        }


    }

}