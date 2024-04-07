package com.idmt.simplevoice.ui.login

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.idmt.simplevoice.constants.userType
import com.idmt.simplevoice.constants.userTypeEnum
import com.idmt.simplevoice.ui.home.components.LoadingButton
import kotlinx.coroutines.delay

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

@Composable
fun LoginScreen(modifier: Modifier, navigateToHomeScreen: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var context = LocalContext.current


    LaunchedEffect(loading) {
        if (loading && email.equals("admin") && password.equals("admin")) {
            delay(3000)
            navigateToHomeScreen.invoke()
            context.dataStore.edit { prefrences ->
                prefrences[userType] = userTypeEnum.Normal.name
            }
        } else {
            loading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
        )

        Spacer(modifier = modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = modifier.height(10.dp))
        LoadingButton(onClick = {
            loading = true

        }, loading = loading, showIcon = false, buttonText = "Login")
    }
}

