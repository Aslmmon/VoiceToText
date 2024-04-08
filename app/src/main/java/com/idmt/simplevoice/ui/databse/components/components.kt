package com.idmt.simplevoice.ui.databse.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R
import com.idmt.simplevoice.constants.userTypeEnum
import com.idmt.simplevoice.ui.databse.data.SECIONS
import com.idmt.simplevoice.ui.databse.data.getAllSecions
import com.idmt.simplevoice.ui.network.model.ListResponseItem

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
fun Loader(modifier: Modifier){
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
