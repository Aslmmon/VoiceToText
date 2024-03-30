package com.idmt.simplevoice.ui.databse

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R
import com.idmt.simplevoice.ui.network.model.ListResponseItem

val dateRanges = listOf("Today", "This Week", "This Month", "Custom")

@SuppressLint("UnrememberedMutableState")
@Composable
fun Database(modifier: Modifier, viewModel: DataBaseViewModel) {
    val uiState by viewModel.listState.collectAsState()
    val sectionsState by viewModel.SectionStates.collectAsState()

//    val selectedSection: MutableState<SECIONS?> = mutableStateOf(null)

    LaunchedEffect(sectionsState) {
        viewModel.getList(1, sectionsState.value)
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
                        itemView(modifier, item)

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

@Composable
fun itemView(modifier: Modifier, item: ListResponseItem) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(text = stringResource(R.string.date) + item.actdate, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = modifier.height(10.dp))
        Text(
            text = stringResource(R.string.section_name) + item.sectionname,
            fontWeight = FontWeight.Bold
        )
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