package com.example.fitfast.ui.screens.strength

import android.content.Context
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.fitfast.model.StrengthExercise
import com.example.fitfast.model.StrengthRoutine
import com.example.fitfast.model.StrengthRoutineViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthWorkoutScreen(
    navController: NavHostController,
    viewModel: StrengthRoutineViewModel,
    routineId: Int
) {
    val routine by viewModel.getRoutine(routineId).observeAsState()
    val lastCompletedWorkout = viewModel.getLastCompletedWorkout(routineId)
    val completedSets = remember { mutableStateMapOf<String, Boolean>() }
    val weightValues = remember { mutableStateMapOf<String, String>() }
    val repsValues = remember { mutableStateMapOf<String, String>() }
    var isFinished by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (!isFinished) {
            delay(1000L)
            elapsedTime += 1
        }
    }

    routine?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 56.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = it.name,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTime(elapsedTime),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(it.exercises) { exercise ->
                    ExerciseSection(navController, exercise, completedSets, weightValues, repsValues, lastCompletedWorkout, viewModel, routineId)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Abort Workout")
                }
                Button(
                    onClick = {
                        if (checkForPR(it.exercises, lastCompletedWorkout, weightValues, repsValues)) {
                            showDialog = true
                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(500)
                        }
                        viewModel.saveCompletedWorkout(it, completedSets, weightValues, repsValues)
                        isFinished = true
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Workout")
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        PRDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
fun ExerciseSection(
    navController: NavHostController,
    exercise: StrengthExercise,
    completedSets: MutableMap<String, Boolean>,
    weightValues: MutableMap<String, String>,
    repsValues: MutableMap<String, String>,
    lastCompletedWorkout: StrengthRoutine?,
    viewModel: StrengthRoutineViewModel,
    routineId: Int
) {
    val lastCompletedSetValues by remember {
        mutableStateOf(viewModel.getLastCompletedSetValues(routineId, exercise.name))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable {
                    val exerciseId = exercise.name.replace(" ", "_").replace("/", "_")
                    navController.navigate("exerciseDetail2/${exerciseId}")
                }
            )
            repeat(exercise.sets) { index ->
                val setKey = "${exercise.name}-$index"
                val lastWeight = lastCompletedSetValues[setKey]?.first?.toString() ?: ""
                val lastReps = lastCompletedSetValues[setKey]?.second?.toString() ?: ""
                SetRow(exercise.name, index, exercise.reps, exercise.weight, completedSets, weightValues, repsValues, lastWeight, lastReps)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SetRow(
    exerciseName: String,
    setIndex: Int,
    defaultReps: Int,
    defaultWeight: Int,
    completedSets: MutableMap<String, Boolean>,
    weightValues: MutableMap<String, String>,
    repsValues: MutableMap<String, String>,
    lastWeight: String,
    lastReps: String
) {
    var weight by remember { mutableStateOf(lastWeight.ifEmpty { defaultWeight.toString() }) }
    var reps by remember { mutableStateOf(lastReps.ifEmpty { defaultReps.toString() }) }
    val setKey = "$exerciseName-$setIndex"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set ${setIndex + 1}:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
                weightValues[setKey] = it
            },
            label = { Text("kg") },
            modifier = Modifier.weight(2f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = reps,
            onValueChange = {
                reps = it
                repsValues[setKey] = it
            },
            label = { Text("Reps") },
            modifier = Modifier.weight(2f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = completedSets[setKey] ?: false,
            onCheckedChange = { completedSets[setKey] = it },
            modifier = Modifier.weight(1f)
        )
    }
}

fun checkForPR(
    exercises: List<StrengthExercise>,
    lastCompletedWorkout: StrengthRoutine?,
    weightValues: Map<String, String>,
    repsValues: Map<String, String>
): Boolean {
    if (lastCompletedWorkout == null) return false

    exercises.forEach { exercise ->
        repeat(exercise.sets) { index ->
            val setKey = "${exercise.name}-$index"
            val lastExercise = lastCompletedWorkout.exercises.find { it.name == exercise.name }
            val lastWeight = lastExercise?.weight ?: 0
            val lastReps = lastExercise?.reps ?: 0
            val currentWeight = weightValues[setKey]?.toIntOrNull() ?: 0
            val currentReps = repsValues[setKey]?.toIntOrNull() ?: 0

            if ((currentReps == lastReps && currentWeight > lastWeight) || (currentWeight == lastWeight && currentReps > lastReps)) {
                return true
            }
        }
    }
    return false
}

@Composable
fun PRDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .clickable(enabled = false) { },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Congratulations!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You've set a new personal record!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
