package com.example.fitfast.ui.screens.strength

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.fitfast.model.StrengthExercise
import com.example.fitfast.model.StrengthRoutine
import com.example.fitfast.model.StrengthRoutineViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddStrengthRoutineDialog(
    viewModel: StrengthRoutineViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onAddRoutine: (StrengthRoutine) -> Unit
) {
    var routineName by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val searchResults by viewModel.searchExercises(searchQuery).observeAsState(initial = emptyList())
    val existingRoutines by viewModel.allRoutines.observeAsState(initial = emptyList())
    var exercises by rememberSaveable { mutableStateOf(listOf<StrengthExercise>()) }
    var isError by remember { mutableStateOf(false) }

    val gson = Gson()
    val selectedExerciseJson: String? = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.get("selectedExercise")

    LaunchedEffect(selectedExerciseJson) {
        selectedExerciseJson?.let {
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedExercise")
            val selectedExercise = gson.fromJson(it, StrengthExercise::class.java)
            exercises = exercises + selectedExercise
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Strength Routine", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError && routineName.isEmpty()
                )
                if (isError && routineName.isEmpty()) {
                    Text(
                        text = "Routine name cannot be blank",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchExercises(it)
                    },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("Search Exercises") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    trailingIcon = {
                        if (isSearchActive) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Search",
                                modifier = Modifier.clickable { isSearchActive = false }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn {
                        items(searchResults) { exercise ->
                            Text(
                                text = exercise.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            exercises = exercises + StrengthExercise(
                                                name = exercise.name,
                                                sets = 0,
                                                reps = 0,
                                                weight = 0
                                            )
                                        },
                                        onLongClick = {
                                            navController.navigate("exerciseDetail/${exercise.id}")
                                        }
                                    )
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Added Exercises:", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(exercises) { exercise ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = exercise.name,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { exercises = exercises - exercise }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove Exercise")
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = exercise.sets.toString(),
                                onValueChange = { newSets ->
                                    exercises = exercises.map {
                                        if (it.name == exercise.name) it.copy(sets = newSets.toIntOrNull() ?: 0) else it
                                    }
                                },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = exercise.reps.toString(),
                                onValueChange = { newReps ->
                                    exercises = exercises.map {
                                        if (it.name == exercise.name) it.copy(reps = newReps.toIntOrNull() ?: 0) else it
                                    }
                                },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = exercise.weight.toString(),
                                onValueChange = { newWeight ->
                                    exercises = exercises.map {
                                        if (it.name == exercise.name) it.copy(weight = newWeight.toIntOrNull() ?: 0) else it
                                    }
                                },
                                label = { Text("Weight") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (routineName.isNotBlank() && exercises.isNotEmpty()) {
                            val newRoutine = StrengthRoutine(
                                id = (existingRoutines.maxOfOrNull { it.id } ?: 0) + 1,
                                name = routineName,
                                exercises = exercises
                            )
                            onAddRoutine(newRoutine)
                            onDismiss()
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Routine")
                }
            }
        }
    }
}

/*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.fitfast.model.StrengthExercise
import com.example.fitfast.model.StrengthRoutine
import com.example.fitfast.model.StrengthRoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStrengthRoutineDialog(
    viewModel: StrengthRoutineViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onAddRoutine: (StrengthRoutine) -> Unit
) {
    var routineName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val searchResults by viewModel.searchExercises(searchQuery).observeAsState(initial = emptyList())
    var exercises by remember { mutableStateOf(listOf<StrengthExercise>()) }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Strength Routine", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError && routineName.isEmpty()
                )
                if (isError && routineName.isEmpty()) {
                    Text(
                        text = "Routine name cannot be blank",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = isError && sets.toIntOrNull() == null
                )
                if (isError && sets.toIntOrNull() == null) {
                    Text(
                        text = "Sets must be a valid number",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = isError && reps.toIntOrNull() == null
                )
                if (isError && reps.toIntOrNull() == null) {
                    Text(
                        text = "Reps must be a valid number",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchExercises(it)
                    },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("Search Exercises") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn {
                        items(searchResults) { exercise ->
                            Text(
                                text = exercise.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("exerciseDetail/${exercise.id}")
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val validExercises = exercises.filter { it.name.isNotBlank() }
                        if (routineName.isNotBlank() && validExercises.isNotEmpty()) {
                            val newRoutine = StrengthRoutine(
                                id = 0, // Assuming id is auto-generated
                                name = routineName,
                                exercises = validExercises
                            )
                            onAddRoutine(newRoutine)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Routine")
                }
            }
        }
    }
}
 */