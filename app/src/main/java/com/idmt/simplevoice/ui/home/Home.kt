package com.idmt.simplevoice.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.idmt.simplevoice.R
import com.idmt.simplevoice.SharedViewModel
import com.idmt.simplevoice.recognition.VoiceTextParser
import com.idmt.simplevoice.ui.databse.SECIONS
import com.idmt.simplevoice.ui.home.components.CategoryChooser
import com.idmt.simplevoice.ui.home.components.EditText
import com.idmt.simplevoice.ui.home.components.LoadingButton
import com.idmt.simplevoice.ui.network.model.ListResponseItem


const val English = "en-US"
const val Hindi = "hi"
val languagesList = mutableListOf(
    "English",
    "Hindi",
)


@Composable
fun Home(
    modifier: Modifier,
    voiceTextParser: VoiceTextParser,
    homeViewModel: HomeViewModel,
    sharedViewModel: SharedViewModel<ListResponseItem>
) {

    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val textSpokenState by homeViewModel.textSpoken.collectAsState()
    var canRecord by rememberSaveable {
        mutableStateOf(false)
    }
    var languageToRecordWith by rememberSaveable {
        mutableStateOf(English)
    }
    val state by voiceTextParser.state.collectAsState()
    var loading by remember { mutableStateOf(false) }

    val recordLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                canRecord = isGranted
            })

    var listOfPairs = mutableListOf(Pair("", 0))
    SECIONS.entries.forEach {
        listOfPairs.add(Pair(it.name,it.value))
    }
    LaunchedEffect(recordLauncher) {
        recordLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }


    DisposableEffect(state.appendText) {

        onDispose {
            if (state.appendText == true) {
                homeViewModel.updateText(textSpokenState + " " + state.isSpoken + " ")
            }
            state.appendText = false
        }

    }

    LaunchedEffect(sharedViewModel.isEdit) {

        if (sharedViewModel.isEdit.value) {
            homeViewModel.updateText(sharedViewModel.data.value?.note ?: "")
            homeViewModel.updateSectionDataId(sharedViewModel.data.value?.section_Dataid ?: 0)
        }


    }


    Scaffold(floatingActionButton = {

    }) { padding ->

        Column(modifier.fillMaxSize()) {

            Image(
                painterResource(R.drawable.logo),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp, 100.dp)
                    .padding(vertical = 5.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(R.string.intelligence_data_management_tool_text),
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center
            )


        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier.fillMaxSize()
        ) {
            when (homeUiState) {
                is HomeViewModel.HomeUiState.Loading -> {
                    loading = true
                }

                is HomeViewModel.HomeUiState.Success -> {
                    loading = false
                }

                is HomeViewModel.HomeUiState.Error -> {

                }

                else -> {}
            }
            EditText(textEnterd = textSpokenState) { text ->
                homeViewModel.updateText(text)
            }
            Spacer(modifier = modifier.height(10.dp))
            AnimatedContent(
                modifier = Modifier.padding(padding),
                targetState = state.isSpeaking
            ) {

                if (it) {
                    Text(text = "speak now please ... ")
                } else {
                    Text(text = "Click on Mic to Record voice")
                }

            }
            Spacer(modifier = modifier.height(20.dp))
            Row(
                modifier = modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(text = "EN") },
                    backgroundColor = Color.Gray.copy(alpha = 0.8f),
                    onClick = {
                        languageToRecordWith = English
                        if (state.isSpeaking) {
                            voiceTextParser.stopListening()
                        } else {
                            voiceTextParser.startListening(languages = languageToRecordWith)
                        }
                    },
                    icon = {
                        Icon(
                            if (state.isSpeaking) Icons.Filled.Stop else Icons.Filled.Mic,
                            ""
                        )
                    }
                )
                ExtendedFloatingActionButton(
                    text = { Text(text = "HI") },
                    backgroundColor = Color.Gray.copy(alpha = 0.8f),
                    onClick = {
                        languageToRecordWith = Hindi
                        if (state.isSpeaking) {
                            voiceTextParser.stopListening()
                        } else {
                            voiceTextParser.startListening(languages = languageToRecordWith)
                        }
                    },
                    icon = {
                        Icon(
                            if (state.isSpeaking) Icons.Filled.Stop else Icons.Filled.Mic,
                            ""
                        )
                    }
                )
                ExtendedFloatingActionButton(
                    text = { Text(text = "Clear") },
                    backgroundColor = Color.Gray.copy(alpha = 0.8f),
                    onClick = {
                        homeViewModel.updateText("")
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Clear,
                            ""
                        )
                    }
                )
            }

            CategoryChooser(modifier = modifier.padding(horizontal = 10.dp),listOfPairs) { sectionId ->
                homeViewModel.updateSection(sectionId)
            }


            LoadingButton(
                onClick = {
                    homeViewModel.submitText()
                },
                loading = loading,
                showIcon = true,
                buttonText = if (sharedViewModel.isEdit.value) "Edit" else "Save"
            )
            Spacer(modifier = modifier.height(50.dp))

        }
    }
}
