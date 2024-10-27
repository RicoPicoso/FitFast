package com.example.fitfast

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitfast.model.CardioViewModel
import com.example.fitfast.model.FlexibilityRoutineViewModel
import com.example.fitfast.model.StrengthRoutineViewModel
import com.example.fitfast.ui.screens.cardio.CardioScreen
import com.example.fitfast.ui.screens.cardio.HIITWorkoutScreen
import com.example.fitfast.ui.screens.cardio.LISSWorkoutScreen
import com.example.fitfast.ui.screens.flexibility.FlexibilityScreen
import com.example.fitfast.ui.screens.flexibility.WorkoutHistoryScreen
import com.example.fitfast.ui.screens.flexibility.WorkoutScreen
import com.example.fitfast.ui.screens.strength.ExerciseDetailScreen
import com.example.fitfast.ui.screens.strength.ExerciseListScreen
import com.example.fitfast.ui.screens.strength.StrengthScreen
import com.example.fitfast.ui.screens.strength.StrengthWorkoutScreen
import com.example.fitfast.ui.theme.FitFastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitFastTheme {
                FitFastApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FitFastApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Navigation(navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Cardio,
        BottomNavItem.Strength,
        BottomNavItem.Flexibility
    )
    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    val flexibilityRoutineViewModel: FlexibilityRoutineViewModel = viewModel()
    val strengthRoutineViewModel: StrengthRoutineViewModel = viewModel()
    val cardioRoutineViewModel: CardioViewModel = viewModel()
    NavHost(navController, startDestination = BottomNavItem.Strength.route) {
        composable(BottomNavItem.Cardio.route) {
            CardioScreen(navController, cardioRoutineViewModel)
        }
        composable(BottomNavItem.Strength.route) {
            StrengthScreen(navController, strengthRoutineViewModel)
        }
        composable(BottomNavItem.Flexibility.route) {
            FlexibilityScreen(navController, flexibilityRoutineViewModel)
        }
        composable("workout/{routineId}") { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")?.toInt()
            val routine = flexibilityRoutineViewModel.allRoutines.value?.find { it.id == routineId }
            routine?.let {
                WorkoutScreen(navController = navController, routine = it, onStopWorkout = {
                    flexibilityRoutineViewModel.addWorkoutHistory(it.name, System.currentTimeMillis())
                    navController.popBackStack()
                })
            }
        }
        composable("StrengthWorkout/{routineId}") { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")?.toInt()
            val routine = strengthRoutineViewModel.allRoutines.value?.find { it.id == routineId }
            routine?.let {
                if (routineId != null) {
                    StrengthWorkoutScreen(navController = navController, viewModel = strengthRoutineViewModel, routineId =routineId)
                }
            }
        }
        composable("exerciseDetail/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: return@composable
            ExerciseDetailScreen(exerciseId = exerciseId ?: "", navController = navController, viewModel = strengthRoutineViewModel, addBool = true )
        }
        composable("exerciseDetail2/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: return@composable
            ExerciseDetailScreen(exerciseId = exerciseId ?: "", navController = navController, viewModel = strengthRoutineViewModel, addBool = false)
        }
        composable("exerciseList") {
            ExerciseListScreen(navController, strengthRoutineViewModel)
        }
        composable("workoutHistory") {
            WorkoutHistoryScreen(flexibilityRoutineViewModel, navController)
        }
        composable("lissWorkout"){
            LISSWorkoutScreen(navController, cardioRoutineViewModel)
        }
        composable("hiitWorkout"){
            HIITWorkoutScreen(navController, cardioRoutineViewModel)
        }
    }
}

sealed class BottomNavItem(val title: String, val route: String, val icon: Int) {
    object Cardio : BottomNavItem("Cardio", "cardio", R.drawable.directions_run_24px)
    object Strength : BottomNavItem("Strength", "strength", R.drawable.exercise_24px)
    object Flexibility : BottomNavItem("Flexibility", "flexibility", R.drawable.physical_therapy_24px)
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

