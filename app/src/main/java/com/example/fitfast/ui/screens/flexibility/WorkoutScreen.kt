package com.example.fitfast.ui.screens.flexibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitfast.model.FlexibilityExercise
import com.example.fitfast.model.FlexibilityRoutine
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(
    navController: NavHostController,
    routine: FlexibilityRoutine,
    onStopWorkout: () -> Unit
) {
    if (routine.exercises.isEmpty()) {
        Text("No exercises available")
        return
    }

    var currentExerciseIndex by remember { mutableStateOf(0) }
    var timerValue by remember { mutableStateOf(5) }
    var isResting by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isGettingReady by remember { mutableStateOf(true) }

    LaunchedEffect(timerValue, isPaused) {
        if (timerValue > 0 && !isPaused) {
            delay(1000L)
            timerValue--
        } else if (timerValue == 0) {
            if (isGettingReady) {
                isGettingReady = false
                timerValue = routine.exercises[currentExerciseIndex].duration
            } else if (isResting) {
                if (currentExerciseIndex < routine.exercises.size - 1) {
                    currentExerciseIndex++
                    timerValue = routine.exercises[currentExerciseIndex].duration
                    isResting = false
                } else {
                    onStopWorkout()
                }
            } else {
                if (currentExerciseIndex < routine.exercises.size - 1) {
                    timerValue = routine.restTime
                    isResting = true
                } else {
                    onStopWorkout()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isGettingReady -> "Get Ready"
                isResting -> "Rest Time"
                else -> routine.exercises[currentExerciseIndex].name
            },
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(80.dp))

        CircularProgressIndicator(
            progress = {
                when {
                    isGettingReady -> timerValue / 5f
                    isResting -> timerValue / routine.restTime.toFloat()
                    else -> timerValue / routine.exercises[currentExerciseIndex].duration.toFloat()
                }
            },
            modifier = Modifier.size(120.dp),
            strokeWidth = 8.dp,
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = timerValue.toString(),
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(50.dp))

        Row {
            Button(
                onClick = { isPaused = !isPaused },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = if (isPaused) "Go" else "Pause")
            }
            Button(
                onClick = {
                    if (currentExerciseIndex < routine.exercises.size - 1) {
                        currentExerciseIndex++
                        timerValue = routine.exercises[currentExerciseIndex].duration
                        isResting = false
                    } else {
                        onStopWorkout()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Next Exercise")
            }
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Stop")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWorkoutScreen() {
    val exercises = listOf(
        FlexibilityExercise(name = "Toe Touch", duration = 60),
        FlexibilityExercise(name = "Side Stretch", duration = 60),
        FlexibilityExercise(name = "Neck Stretch", duration = 60)
    )
    val routine = FlexibilityRoutine(id = 1, name = "Morning Stretch", restTime = 30, exercises = exercises)

    val navController = rememberNavController()

    WorkoutScreen(navController = navController,
        routine = routine,
        onStopWorkout = {}
    )
}