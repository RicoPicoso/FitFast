package com.example.fitfast.ui.screens.cardio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.fitfast.model.CardioViewModel
import com.example.fitfast.model.UserData

@Composable
fun CardioScreen(navController: NavHostController, viewModel: CardioViewModel) {
    var showUserDataDialog by remember { mutableStateOf(false) }
    val userData by viewModel.userData.observeAsState()

    LaunchedEffect(Unit) {
        if (userData == null) {
            showUserDataDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cardio Workouts",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        val totalCaloriesBurned by viewModel.totalCaloriesBurnedThisWeek.observeAsState(0)

        Text(
            text = "Total Calories Burned This Week: $totalCaloriesBurned",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))

        CardioButton(
            text = "Start HIIT Workout",
            onClick = { navController.navigate("hiitWorkout") },
            backgroundColor = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        CardioButton(
            text = "Start LISS Workout",
            onClick = { navController.navigate("lissWorkout") },
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        CardioButton(
            text = "Edit User Data",
            onClick = { showUserDataDialog = true },
            backgroundColor = MaterialTheme.colorScheme.error
        )

        if (showUserDataDialog) {
            UserDataDialog(
                initialData = userData,
                onDismiss = { showUserDataDialog = false },
                onSave = { newUserData ->
                    viewModel.saveUserData(newUserData)
                    showUserDataDialog = false
                }
            )
        }
    }
}

@Composable
fun CardioButton(text: String, onClick: () -> Unit, backgroundColor: Color) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun UserDataDialog(
    initialData: UserData?,
    onDismiss: () -> Unit,
    onSave: (UserData) -> Unit
) {
    var weight by remember { mutableStateOf(initialData?.weight?.toString() ?: "") }
    var age by remember { mutableStateOf(initialData?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(initialData?.gender ?: "") }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Enter User Data", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                gender = "Male"
                                expanded = false
                            },
                            text = { Text("Male") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                gender = "Female"
                                expanded = false
                            },
                            text = { Text("Female") }
                        )
                    }
                }

                Button(
                    onClick = {
                        val validWeight = weight.toDoubleOrNull()
                        val validAge = age.toIntOrNull()
                        if (validWeight != null && validAge != null && gender.isNotBlank()) {
                            onSave(UserData(validWeight, validAge, gender))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Save", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
