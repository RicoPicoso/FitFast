package com.example.fitfast.ui.screens.flexibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitfast.model.FlexibilityExercise
import com.example.fitfast.model.FlexibilityRoutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlexibilityRoutineDialog(
    existingRoutines: List<FlexibilityRoutine>,
    onDismiss: () -> Unit,
    onAddRoutine: (FlexibilityRoutine) -> Unit
) {
    var routineName by rememberSaveable { mutableStateOf("") }
    var restTime by rememberSaveable { mutableStateOf("") }
    var exercises by rememberSaveable { mutableStateOf(listOf(FlexibilityExercise("", 0))) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var isDuplicateError by rememberSaveable { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Flexibility Routine",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routineName,
                    onValueChange = {
                        routineName = it
                        isDuplicateError =
                            existingRoutines.any { routine -> routine.name == routineName }
                    },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError && routineName.isEmpty() || isDuplicateError
                )
                if (isError && routineName.isEmpty()) {
                    Text(
                        text = "Routine name cannot be blank",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (isDuplicateError) {
                    Text(
                        text = "Routine name already exists",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = restTime,
                    onValueChange = { restTime = it },
                    label = { Text("Rest Time (in seconds)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = isError && restTime.toIntOrNull() == null
                )
                if (isError && restTime.toIntOrNull() == null) {
                    Text(
                        text = "Rest time must be a valid number",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Exercises",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(exercises.size) { index ->
                            ExerciseInput(
                                exercise = exercises[index],
                                onNameChange = { newName ->
                                    exercises = exercises.toMutableList().apply {
                                        this[index] = exercises[index].copy(name = newName)
                                    }
                                },
                                onDurationChange = { newDuration ->
                                    exercises = exercises.toMutableList().apply {
                                        this[index] = exercises[index].copy(
                                            duration = newDuration.toIntOrNull() ?: 0
                                        )
                                    }
                                },
                                onDelete = {
                                    exercises = exercises.toMutableList().apply {
                                        removeAt(index)
                                    }
                                },
                                isError = isError && (exercises[index].name.isEmpty() || exercises[index].duration <= 0)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = {
                            exercises = exercises + FlexibilityExercise("", 0)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .width(136.dp)
                    ) {
                        Text("Add Exercise", fontSize = 15.sp)
                    }

                    Button(
                        onClick = {
                            val validExercises =
                                exercises.filter { it.name.isNotBlank() && it.duration > 0 }
                            if (routineName.isNotBlank() && restTime.toIntOrNull() != null && validExercises.isNotEmpty()) {
                                val newRoutine = FlexibilityRoutine(
                                    id = existingRoutines.maxOfOrNull { it.id }?.plus(1) ?: 1,
                                    name = routineName,
                                    restTime = restTime.toInt(),
                                    exercises = validExercises
                                )
                                onAddRoutine(newRoutine)
                                onDismiss()
                            } else {
                                isError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .width(136.dp)
                    ) {
                        Text("Add Routine", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseInput(
    exercise: FlexibilityExercise,
    onNameChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onDelete: () -> Unit,
    isError: Boolean
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = exercise.name,
                onValueChange = onNameChange,
                label = { Text("Exercise Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isError = isError && exercise.name.isEmpty()
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick =  onDelete ) {
                Icon(Icons.Default.Close, contentDescription = "Remove Exercise")
            }
        }
        if (isError && exercise.name.isEmpty()) {
            Text(
                text = "Exercise name cannot be blank",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = exercise.duration.toString(),
        onValueChange = onDurationChange,
        label = { Text("Duration (in seconds)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isError = isError && exercise.duration <= 0
    )
        if (isError && exercise.duration <= 0) {
            Text(
                text = "Duration must be a positive number",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
