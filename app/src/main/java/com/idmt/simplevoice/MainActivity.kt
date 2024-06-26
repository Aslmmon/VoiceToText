package com.idmt.simplevoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Input
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.idmt.simplevoice.recognition.VoiceTextParser
import com.idmt.simplevoice.ui.InputEntry.InputEntry
import com.idmt.simplevoice.ui.InputEntry.InputViewModel
import com.idmt.simplevoice.ui.databse.DataBaseViewModel
import com.idmt.simplevoice.ui.databse.Database
import com.idmt.simplevoice.ui.home.Home
import com.idmt.simplevoice.ui.home.HomeViewModel
import com.idmt.simplevoice.ui.login.LoginScreen
import com.idmt.simplevoice.ui.network.model.ListResponseItem
import com.idmt.simplevoice.ui.theme.SimpleVoiceTheme

val KEY_ROUTE: String? = "route"


class MainActivity : ComponentActivity() {

    val voiceTextParser by lazy {
        VoiceTextParser(app = application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleVoiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(modifier = Modifier) {
                                navController.navigate("MainScreen")
                            }
                        }
                        composable("MainScreen") {
                            MainScreen(voiceTextParser = voiceTextParser)
                        }

                    }


                }
            }
        }
    }
}


@Composable
fun MainScreen(voiceTextParser: VoiceTextParser) {

    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        BottomNavigationScreens.Home,
        BottomNavigationScreens.DataBase,
        BottomNavigationScreens.InputEntryScreen,

        )
    Scaffold(
        bottomBar = {
            SpookyAppBottomNavigation(navController, bottomNavigationItems)
        }
    ) {
        MainScreenNavigationConfigurations(navController, it, voiceTextParser)
    }
}

sealed class BottomNavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavigationScreens("Home", resourceId = R.string.home, Icons.Filled.Home)
    object DataBase : BottomNavigationScreens(
        "DataBase",
        resourceId = R.string.Database,
        Icons.Filled.Cached
    )

    object InputEntryScreen : BottomNavigationScreens(
        "Input",
        resourceId = R.string.input,
        Icons.Filled.Input
    )
}

@Composable
private fun SpookyAppBottomNavigation(
    navController: NavHostController,
    items: List<BottomNavigationScreens>
) {
    BottomNavigation(backgroundColor = Color.White) {

        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(id = screen.resourceId)) },

                selected = currentRoute == screen.route,
                onClick = {
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination

                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues,
    voiceTextParser: VoiceTextParser
) {
    val dataBaseViewModel = DataBaseViewModel()
    val homeViewModel = HomeViewModel()
    val inputViewModel = InputViewModel()
    val sharedViewModel = SharedViewModel<ListResponseItem>()

    NavHost(
        navController,
        startDestination = BottomNavigationScreens.Home.route,
        Modifier.padding(paddingValues)
    ) {
        composable(BottomNavigationScreens.Home.route) {
            Home(modifier = Modifier, voiceTextParser, homeViewModel, sharedViewModel)
        }
        composable(BottomNavigationScreens.DataBase.route) {
            Database(modifier = Modifier, dataBaseViewModel, returnBackHome = {
                navController.navigate(BottomNavigationScreens.Home.route)
            }, sharedViewModel)
        }
        composable(BottomNavigationScreens.InputEntryScreen.route) {
            InputEntry(modifier = Modifier, inputViewModel)
        }
    }
}

class SharedViewModel<T> : ViewModel() {
    private val _data = mutableStateOf<T?>(null)
    val data: State<T?> = _data

    private val _isEdit = mutableStateOf<Boolean>(false)
    val isEdit: State<Boolean> = _isEdit

    fun setData(newData: T) {
        _data.value = newData
        _isEdit.value = true
    }

    fun clearData() {
        _data.value = null
        _isEdit.value = false
    }
}


