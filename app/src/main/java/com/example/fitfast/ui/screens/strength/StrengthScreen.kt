package com.example.fitfast.ui.screens.strength

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import com.example.fitfast.model.StrengthRoutine
import com.example.fitfast.model.StrengthRoutineViewModel
import com.example.fitfast.utils.ConnectivityChecker

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StrengthScreen(navController: NavHostController, viewModel: StrengthRoutineViewModel) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveDialog by rememberSaveable { mutableStateOf(false) }
    val isConnected by remember { mutableStateOf(ConnectivityChecker.isNetworkAvailable(context)) }
    val routines by viewModel.allRoutines.observeAsState(emptyList())
    val networkError by viewModel.networkError.observeAsState(false)

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp, end = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        AddStrengthRoutineButton(onAdd = { showDialog = true })
                        RemoveStrengthRoutineButton(onRemove = { showRemoveDialog = true })
                    }
                    BrowseExercisesButton(onBrowse = {
                        if (isConnected) {
                            navController.navigate("exerciseList")
                        } else {
                        }
                    })
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
            ) {
                Text("Strength Routines", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                if (networkError) {
                    Text(
                        text = "No internet connection. Some features may not work.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                StrengthRoutineList(
                    routines = routines,
                    onRoutineClick = { routine ->
                        navController.navigate("StrengthWorkout/${routine.id}")
                    },
                    onRoutineLongClick = {}
                )

                if (showDialog) {
                    AddStrengthRoutineDialog(
                        viewModel = viewModel,
                        navController = navController,
                        onDismiss = { showDialog = false },
                        onAddRoutine = { routine ->
                            viewModel.addRoutine(routine)
                            showDialog = false
                        }
                    )
                }

                if (showRemoveDialog) {
                    RemoveStrengthRoutineDialog(
                        routines = routines,
                        onDismiss = { showRemoveDialog = false },
                        onRemoveRoutine = { routine ->
                            viewModel.deleteRoutine(routine)
                            showRemoveDialog = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AddStrengthRoutineButton(onAdd: () -> Unit) {
    FloatingActionButton(
        onClick = onAdd,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }
}

@Composable
fun RemoveStrengthRoutineButton(onRemove: () -> Unit) {
    FloatingActionButton(
        onClick = onRemove,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Remove")
    }
}

@Composable
fun BrowseExercisesButton(onBrowse: () -> Unit) {
    Button(
        onClick = onBrowse,
        modifier = Modifier
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA683E3)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text("Search Exercises", fontSize = 16.sp)
    }
}

@Composable
fun StrengthRoutineList(
    routines: List<StrengthRoutine>,
    onRoutineClick: (StrengthRoutine) -> Unit,
    onRoutineLongClick: (StrengthRoutine) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(routines) { routine ->
            StrengthRoutineItem(routine, onRoutineClick, onRoutineLongClick)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StrengthRoutineItem(
    routine: StrengthRoutine,
    onRoutineClick: (StrengthRoutine) -> Unit,
    onRoutineLongClick: (StrengthRoutine) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onRoutineClick(routine) },
                    onLongClick = { showMenu = true }
                )
                .padding(16.dp)
        ) {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (showMenu) {
            Popup(
                onDismissRequest = { showMenu = false }
            ) {
                Surface(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column {
                        Text(
                            "Edit Routine",
                            modifier = Modifier
                                .clickable {
                                    onRoutineLongClick(routine)
                                    showMenu = false
                                }
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
