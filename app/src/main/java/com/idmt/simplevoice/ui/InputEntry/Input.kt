package com.idmt.simplevoice.ui.InputEntry

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import com.idmt.simplevoice.ui.home.components.EditText
import com.idmt.simplevoice.ui.home.components.LoadingButton
import kotlinx.coroutines.delay

@Composable
fun InputEntry(modifier: Modifier) {
    var loading by remember { mutableStateOf(false) }
    var textEnterd by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        DropDownEditText(
            modifier = modifier,
            label = stringResource(R.string.input_type),
            onChoosenId = {
                Log.e("chosen", it.toString())
            },
            listToBeShown = mutableListOf(Pair("test", 1), Pair("test2", 2))
        )

        DropDownEditText(
            modifier = modifier,
            label = stringResource(R.string.sub_type),
            onChoosenId = {
                Log.e("chosen", it.toString())
            },
            listToBeShown = mutableListOf(Pair("test", 1), Pair("test2", 2))
        )

        DropDownEditText(modifier = modifier, label = stringResource(R.string.zone), onChoosenId = {
            Log.e("chosen", it.toString())
        }, listToBeShown = mutableListOf(Pair("test", 1), Pair("test2", 2)))

        DropDownEditText(
            modifier = modifier,
            label = stringResource(R.string.district),
            onChoosenId = {
                Log.e("chosen", it.toString())
            },
            listToBeShown = mutableListOf(Pair("test", 1), Pair("test2", 2))
        )

        DropDownEditText(
            modifier = modifier,
            label = stringResource(R.string.station),
            onChoosenId = {
                Log.e("chosen", it.toString())
            },
            listToBeShown = mutableListOf(Pair("test", 1), Pair("test2", 2))
        )

        EditText(textEnterd = textEnterd) { text ->
            textEnterd = text
        }

        LoadingButton(modifier= modifier,showIcon = false, onClick = {
            loading = true
        }, loading = loading, buttonText = stringResource(R.string.submit))
    }
}