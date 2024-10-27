package com.example.fitfast.ui.screens.flexibility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.fitfast.model.FlexibilityExercise
import com.example.fitfast.model.FlexibilityRoutine

@Composable
fun RemoveFlexibilityRoutineDialog(
    routines: List<FlexibilityRoutine>,
    onDismiss: () -> Unit,
    onRemoveRoutine: (FlexibilityRoutine) -> Unit
) {
    var selectedRoutine by remember { mutableStateOf<FlexibilityRoutine?>(null) }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Remove Flexibility Routine", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(routines) { routine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRoutine = routine
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedRoutine == routine,
                                onClick = { selectedRoutine = routine }
                            )
                            Text(
                                text = routine.name,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                   /* Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    } */
                    Button(
                        onClick = {
                            selectedRoutine?.let {
                                onRemoveRoutine(it)
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        enabled = selectedRoutine != null
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRemoveFlexibilityRoutineDialog() {
    val exercises = listOf(
        FlexibilityExercise(name = "Toe Touch", duration = 60),
        FlexibilityExercise(name = "Side Stretch", duration = 60),
        FlexibilityExercise(name = "Neck Stretch", duration = 60)
    )
    val routines = listOf(FlexibilityRoutine(id = 1, name = "Morning Stretch", restTime = 30, exercises = exercises),
        FlexibilityRoutine(id = 2, name = "Afternoon Stretch", restTime = 30, exercises = exercises),)

    RemoveFlexibilityRoutineDialog(routines, onDismiss = {}, onRemoveRoutine = {})
}