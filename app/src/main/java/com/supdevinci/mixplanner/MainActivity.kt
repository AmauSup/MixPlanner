package com.supdevinci.mixplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.supdevinci.mixplanner.navigation.CocktailNavHost
import com.supdevinci.mixplanner.navigation.Routes
import com.supdevinci.mixplanner.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route

                    Scaffold(
                        bottomBar = {
                            if (currentRoute != Routes.SPLASH) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentRoute == Routes.HOME,
                                        onClick = {
                                            navController.navigate(Routes.HOME) {
                                                popUpTo(Routes.HOME) { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = {
                                            Icon(Icons.Default.Home, contentDescription = "Accueil")
                                        },
                                        label = { Text("Accueil") }
                                    )

                                    NavigationBarItem(
                                        selected = currentRoute == Routes.SEARCH,
                                        onClick = {
                                            navController.navigate(Routes.SEARCH) {
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = {
                                            Icon(Icons.Default.Search, contentDescription = "Recherche")
                                        },
                                        label = { Text("Recherche") }
                                    )

                                    NavigationBarItem(
                                        selected = currentRoute == Routes.LOCAL,
                                        onClick = {
                                            navController.navigate(Routes.LOCAL) {
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = {
                                            Icon(Icons.Default.List, contentDescription = "Mes listes")
                                        },
                                        label = { Text("Mes listes") }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        CocktailNavHost(
                            navController = navController,
                            searchViewModel = searchViewModel,
                            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}