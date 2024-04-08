package com.idmt.simplevoice.ui.InputEntry

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R
import com.idmt.simplevoice.ui.InputEntry.components.DropDownEditText
import com.idmt.simplevoice.ui.databse.components.ShowLoadingView
import com.idmt.simplevoice.ui.home.components.EditText
import com.idmt.simplevoice.ui.home.components.LoadingButton

@Composable
fun InputEntry(modifier: Modifier, inputViewModel: InputViewModel) {
    var loading by remember { mutableStateOf(false) }
    var textEnterd by remember { mutableStateOf("") }
    val uiState by inputViewModel.categoryState.collectAsState()
    val subCategory by inputViewModel.subCategories.collectAsState()
    val zones by inputViewModel.zones.collectAsState()
    val zoneDistricts by inputViewModel.zoneDistrict.collectAsState()
    val zoneStations by inputViewModel.zoneStations.collectAsState()


    LaunchedEffect(Unit) {
        inputViewModel.getCategories()
        inputViewModel.getZones()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (uiState) {
            is InputViewModel.UiState.Loading -> {
                ShowLoadingView(modifier)
            }

            is InputViewModel.UiState.Error -> {
                Text(text = (uiState as InputViewModel.UiState.Error).message)
            }

            is InputViewModel.UiState.Success -> {
                val CategoriesPairs = (uiState as InputViewModel.UiState.Success).data
                DropDownEditText(
                    modifier = modifier,
                    label = stringResource(R.string.input_type),
                    onChoosenId = {
                        inputViewModel.updateSubCategories(it)
                    },
                    listToBeShown = CategoriesPairs
                )


                DropDownEditText(
                    modifier = modifier,
                    label = stringResource(R.string.sub_type),
                    onChoosenId = {
                        inputViewModel.selectSubCategory(it)
                    },
                    listToBeShown = subCategory
                )

                DropDownEditText(
                    modifier = modifier,
                    label = stringResource(R.string.zone),
                    onChoosenId = {
                        inputViewModel.updateZoneDistricts(it)
                    },
                    listToBeShown = zones
                )

                DropDownEditText(
                    modifier = modifier,
                    label = stringResource(R.string.district),
                    onChoosenId = {
                        inputViewModel.updateZoneStations(it)

                    },
                    listToBeShown = zoneDistricts
                )

                DropDownEditText(
                    modifier = modifier,
                    label = stringResource(R.string.station),
                    onChoosenId = {
                        inputViewModel.selectZoneStation(it)
                    },
                    listToBeShown = zoneStations
                )

                EditText(textEnterd = textEnterd) { text ->
                    textEnterd = text
                    inputViewModel.updateInputData(textEnterd)
                }

                LoadingButton(modifier = modifier, showIcon = false, onClick = {
                    loading = true
                    inputViewModel.submitInputEntry()
                }, loading = loading, buttonText = stringResource(R.string.submit))
            }
        }


    }
}