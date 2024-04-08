package com.idmt.simplevoice.ui.databse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.SharedViewModel
import com.idmt.simplevoice.constants.userType
import com.idmt.simplevoice.ui.databse.components.ChipGroup
import com.idmt.simplevoice.ui.databse.components.Loader
import com.idmt.simplevoice.ui.databse.components.itemView
import com.idmt.simplevoice.ui.databse.data.getAllSecions
import com.idmt.simplevoice.ui.databse.data.getSection
import com.idmt.simplevoice.ui.login.dataStore
import com.idmt.simplevoice.ui.network.model.ListResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@SuppressLint("UnrememberedMutableState")
@Composable
fun Database(
    modifier: Modifier,
    viewModel: DataBaseViewModel,
    returnBackHome: () -> Unit,
    sharedViewModel: SharedViewModel<ListResponseItem>
) {
    val uiState by viewModel.listState.collectAsState()
    val sectionsState by viewModel.SectionStates.collectAsState()
    var context = LocalContext.current
    val userType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[userType] ?: ""
        }
    var user = userType.collectAsState(initial = "")
    var showBottomSheet by remember { mutableStateOf(false) }
    val commentState by viewModel.CommentsState.collectAsState()



    LaunchedEffect(sectionsState) {
        viewModel.getList(1, sectionsState.value)

    }

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            viewModel.getComments(279)
        }
    }
    when (uiState) {
        is DataBaseViewModel.UiState.Loading -> {
            Loader(modifier = modifier)
        }

        is DataBaseViewModel.UiState.Success -> {
            val response = (uiState as DataBaseViewModel.UiState.Success).data
            Column {
                ChipGroup(
                    cars = getAllSecions(),
                    selectedCar = sectionsState,
                    onSelectedChanged = {
                        viewModel.showLoading()
                        getSection(it)?.let { it1 -> viewModel.updateSections(it1) }

                    }
                )
                when (commentState) {
                    is DataBaseViewModel.CommentsUiState.Loading -> {
                        Loader(modifier = modifier)
                    }

                    is DataBaseViewModel.CommentsUiState.Success -> {
                        if (showBottomSheet) {
                            BottomSheet(modifier, onDismiss = {
                                showBottomSheet = false
                            }, updateText = { newText ->
                                viewModel.submitComments(279, newText, 8)
                            }, (commentState as DataBaseViewModel.CommentsUiState.Success).data)
                        }
                    }

                    is DataBaseViewModel.CommentsUiState.SubmitSuccess -> {}
                    is DataBaseViewModel.CommentsUiState.Error -> {}
                    else -> {}
                }
                LazyColumn {
                    items(response) { item ->
                        itemView(modifier, item, user.value,
                            onUpdateClicked = {
                                showBottomSheet = true
                            }, onApproveClicked = {
                            }, onDisapproveClicked = {
                            }, onEditClicked = {
                                Log.e("click", "edit")
                                returnBackHome.invoke()
                                sharedViewModel.setData(item)
                            })

                        Divider(
                            modifier = modifier.padding(10.dp),
                            color = Color.Gray,
                            thickness = 1.dp
                        )

                    }
                }
            }

        }

        is DataBaseViewModel.UiState.Error -> {
            Text((uiState as DataBaseViewModel.UiState.Error).message)

        }

    }


}
