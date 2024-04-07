package com.idmt.simplevoice.ui.InputEntry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R

@Composable
fun DropDownEditText(
    modifier: Modifier,
    onChoosenId: (Int) -> Unit,
    listToBeShown: MutableList<Pair<String, Int>>,
    label: String
) {
    var inputType by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(vertical = 5.dp)) {
        OutlinedTextField(
            modifier = modifier.clickable {
                expandedCategory = true
            }.fillMaxWidth(),
            value = inputType,
            label = { Text(text = label) },
            onValueChange = { inputType = it },
            enabled = false,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            }
        )
        DropdownMenu(
            expanded = expandedCategory,
            onDismissRequest = { expandedCategory = false }
        ) {
            listToBeShown.forEach {
                DropdownMenuItem(
                    text = { Text(it.first) },
                    onClick = {
                        expandedCategory = false
                        inputType = it.first
                        onChoosenId.invoke(it.second)
                    }
                )
            }
        }
    }

}