package com.supdevinci.mixplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.supdevinci.mixplanner.view.CocktailScreen
import com.supdevinci.mixplanner.view.DetailCocktailRouteScreen
import com.supdevinci.mixplanner.view.HomeScreen
import com.supdevinci.mixplanner.view.ListDetailScreen
import com.supdevinci.mixplanner.view.SearchScreen
import com.supdevinci.mixplanner.view.SplashScreen
import com.supdevinci.mixplanner.viewmodel.CocktailListsState
import com.supdevinci.mixplanner.viewmodel.CocktailListsViewModel
import com.supdevinci.mixplanner.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun CocktailNavHost(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                navController = navController,
                modifier = Modifier
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onGoToSearch = { navController.navigate(Routes.SEARCH) },
                onOpenDetails = { cocktailId ->
                    navController.navigate("${Routes.DETAIL}/$cocktailId")
                },
                modifier = Modifier
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = searchViewModel,
                onGoToHome = { navController.navigate(Routes.HOME) },
                onOpenDetails = { cocktailId ->
                    navController.navigate("${Routes.DETAIL}/$cocktailId")
                },
                modifier = Modifier
            )
        }

        composable(Routes.LOCAL) {
            CocktailScreen(
                onListClick = { listId ->
                    navController.navigate("${Routes.LIST_DETAIL}/$listId")
                },
                modifier = Modifier
            )
        }

        composable(
            route = "${Routes.LIST_DETAIL}/{listId}",
            arguments = listOf(
                navArgument("listId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getLong("listId") ?: 0L
            val listsViewModel: CocktailListsViewModel = viewModel()
            val state = listsViewModel.state.collectAsStateWithLifecycle()

            when (val currentState = state.value) {
                is CocktailListsState.Success -> {
                    val list = currentState.lists.firstOrNull { it.list.id == listId }

                    if (list != null) {
                        ListDetailScreen(
                            list = list,
                            onBack = { navController.popBackStack() },
                            onCocktailClick = { cocktailId ->
                                navController.navigate("${Routes.DETAIL}/$cocktailId")
                            },
                            modifier = Modifier
                        )
                    }
                }

                CocktailListsState.Loading -> {}
                CocktailListsState.Empty -> {}
                is CocktailListsState.Error -> {}
            }
        }

        composable(
            route = "${Routes.DETAIL}/{cocktailId}",
            arguments = listOf(
                navArgument("cocktailId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("cocktailId") ?: ""

            DetailCocktailRouteScreen(
                cocktailId = cocktailId,
                onBack = { navController.popBackStack() },
                modifier = Modifier
            )
        }
    }
}