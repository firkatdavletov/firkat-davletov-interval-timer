package com.firkat.intervaltraining.app.navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firkat.intervaltraining.feature.loadworkout.ui.LoadWorkoutRoute
import com.firkat.intervaltraining.feature.training.presentation.TrainingViewModel
import com.firkat.intervaltraining.feature.training.ui.TrainingRoute

@Composable
fun IntervalNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.LoadWorkout.route,
        modifier = modifier,
    ) {
        composable(route = AppDestination.LoadWorkout.route) {
            LoadWorkoutRoute(
                onNavigateToTraining = { workoutId ->
                    navController.navigate(AppDestination.Training.createRoute(workoutId))
                },
            )
        }

        composable(
            route = AppDestination.Training.route,
            arguments = listOf(navArgument(TrainingViewModel.WORKOUT_ID_ARG) {
                type = NavType.StringType
            }),
        ) {
            TrainingRoute()
        }
    }
}
