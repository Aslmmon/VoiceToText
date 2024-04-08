package com.idmt.simplevoice.ui.databse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R
import com.idmt.simplevoice.SharedViewModel
import com.idmt.simplevoice.constants.userType
import com.idmt.simplevoice.constants.userTypeEnum
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
//    val comments by viewModel.Comments.collectAsState()
    val commentState by viewModel.CommentsState.collectAsState()



    LaunchedEffect(sectionsState) {
        viewModel.getList(1, sectionsState.value)

    }

    LaunchedEffect(showBottomSheet) {
//        if (showBottomSheet) {
//        //    viewModel.getComments(279)
//        }
    }
    when (uiState) {
        is DataBaseViewModel.UiState.Loading -> {
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

        is DataBaseViewModel.UiState.Success -> {
            var response = (uiState as DataBaseViewModel.UiState.Success).data


            Column {
                ChipGroup(
                    cars = getAllSecions(),
                    selectedCar = sectionsState,
                    onSelectedChanged = {
                        viewModel.showLoading()
                        getSection(it)?.let { it1 -> viewModel.updateSections(it1) }

                    }
                )
                LazyColumn {
                    items(response) { item ->
                        itemView(modifier, item, user.value,
                            onUpdateClicked = {
                               // showBottomSheet = true
                                viewModel.getComments(279)

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

//                if (showBottomSheet) {
//                    BottomSheet(modifier, onDismiss = {
//                        showBottomSheet = false
//                    }, updateText = { newText ->
//                        viewModel.updateComment(newText)
//                    }, comments)
//                }
            }

        }

        is DataBaseViewModel.UiState.Error -> {
            Text((uiState as DataBaseViewModel.UiState.Error).message)

        }

    }

    when (commentState) {
        is DataBaseViewModel.CommentsUiState.Loading -> {
            CircularProgressIndicator()
        }

        is DataBaseViewModel.CommentsUiState.Success -> {
             showBottomSheet = true
            if (showBottomSheet) {
                BottomSheet(modifier, onDismiss = {
                    showBottomSheet = false
                }, updateText = { newText ->
                    viewModel.submitComments(279, newText, 8)
                }, (commentState as DataBaseViewModel.CommentsUiState.Success).data)
            }
        }

        is DataBaseViewModel.CommentsUiState.SubmitSuccess -> {
            Text(text = "Done")
            CircularProgressIndicator()

        }

        is DataBaseViewModel.CommentsUiState.Error -> {}


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun itemView(
    modifier: Modifier,
    item: ListResponseItem,
    userType: String,
    onUpdateClicked: () -> Unit,
    onApproveClicked: () -> Unit,
    onDisapproveClicked: () -> Unit,
    onEditClicked: () -> Unit


) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(text = stringResource(R.string.date) + item.actdate, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = modifier.height(10.dp))
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.section_name) + item.sectionname,
                fontWeight = FontWeight.Bold,
                color = if (item.isApproved) Color.Green else Color.Red
            )
            if (userType == userTypeEnum.SuperUser.name) {
                Image(
                    modifier = modifier
                        .size(20.dp)
                        .clickable {
                            onApproveClicked.invoke()
                        },
                    painter = painterResource(id = R.drawable.approve),
                    contentDescription = ""
                )
                Image(
                    modifier = modifier
                        .size(15.dp)
                        .clickable {

                            onDisapproveClicked.invoke()
                        },
                    painter = painterResource(id = R.drawable.disapprove),
                    contentDescription = ""
                )
                Image(
                    modifier = modifier
                        .size(15.dp)
                        .combinedClickable(onClick = {
                            onEditClicked.invoke()
                        }, onLongClick = {
                            onUpdateClicked.invoke()
                        }),
                    painter = painterResource(id = R.drawable.pencil),
                    contentDescription = ""
                )

            } else {
                Image(
                    modifier = modifier
                        .size(15.dp)
                        .combinedClickable(onClick = {
                            onEditClicked.invoke()
                        }, onLongClick = {
                            onUpdateClicked.invoke()
                        }),
                    painter = painterResource(id = R.drawable.pencil),
                    contentDescription = ""
                )
            }

        }
        Spacer(modifier = modifier.height(10.dp))
        Text(text = stringResource(R.string.notes) + item.note, fontWeight = FontWeight.Light)

    }
}

@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        elevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) Color.Red else MaterialTheme.colorScheme.primary
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged(name)
                }
            )
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ChipGroup(
    cars: List<SECIONS> = getAllSecions(),
    selectedCar: SECIONS? = null,
    onSelectedChanged: (Int) -> Unit = {},
) {
    Column(modifier = Modifier.padding(8.dp)) {
        LazyRow {
            items(cars) {
                Chip(
                    name = it.toString(),
                    isSelected = selectedCar == it,
                    onSelectionChanged = { name ->
                        onSelectedChanged(cars.find { it.name == name }?.value ?: 0)
                    },
                )
            }
        }
    }
}

enum class SECIONS(val value: Int) {
    Political(1),
    Hindu(2),
    Muslim(3),
    Christian(4),
    YouthANDStudents(5),
    Labour(6),
    Sikh(7),
    Misc(8),

}

fun getAllSecions(): List<SECIONS> {
    return listOf(
        SECIONS.Political,
        SECIONS.Hindu,
        SECIONS.Muslim,
        SECIONS.Christian,
        SECIONS.YouthANDStudents,
        SECIONS.Labour,
        SECIONS.Sikh,
        SECIONS.Misc
    )
}

fun getSection(value: Int): SECIONS? {
    val map = SECIONS.entries.associateBy(SECIONS::value)
    return map[value]
}

