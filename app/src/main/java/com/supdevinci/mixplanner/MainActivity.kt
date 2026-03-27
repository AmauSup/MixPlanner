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
import androidx.compose.ui.Modifier
import com.supdevinci.mixplanner.ui.theme.MixPlannerTheme
import com.supdevinci.mixplanner.view.AppHeader
import androidx.compose.material3.NavigationBarItemDefaults

class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MixPlannerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route

                    Scaffold(
                        topBar = {
                            if (currentRoute != Routes.SPLASH) {
                                AppHeader()
                            }
                        },
                        bottomBar = {
                            if (currentRoute != Routes.SPLASH) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
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
                                        label = { Text("Accueil") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                            indicatorColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
                                        label = { Text("Recherche") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                            indicatorColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
                                        label = { Text("Mes listes") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                            indicatorColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        CocktailNavHost(
                            navController = navController,
                            searchViewModel = searchViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}