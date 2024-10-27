package com.example.fitfast.ui.screens.strength

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.fitfast.model.JsonExercise
import com.example.fitfast.model.StrengthExercise
import com.example.fitfast.model.StrengthRoutineViewModel
import com.google.gson.Gson
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun ExerciseDetailScreen(navController: NavHostController, exerciseId: String, viewModel: StrengthRoutineViewModel, addBool: Boolean) {
    viewModel.getExerciseDetail(exerciseId)
    val exercise by viewModel.exerciseDetail.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercise Detail", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.popBackStack() },
                shape = FloatingActionButtonDefaults.shape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Text("Back")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + 80.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    exercise?.let {
                        ExerciseDetailItem(it, addBool, navController)
                    } ?: run {
                        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    )
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ExerciseDetailItem(exercise: JsonExercise, addBool: Boolean, navController: NavHostController) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { exercise.images.size }
    )

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
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = exercise.name?.uppercase() ?: "N/A",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExerciseDetailTextField(title = "Force", value = exercise.force ?: "N/A")
            ExerciseDetailTextField(title = "Level", value = exercise.level ?: "N/A")
            ExerciseDetailTextField(title = "Mechanic", value = exercise.mechanic ?: "N/A")
            ExerciseDetailTextField(title = "Equipment", value = exercise.equipment ?: "N/A")
            ExerciseDetailTextField(title = "Category", value = exercise.category ?: "N/A")

            exercise.primaryMuscles?.takeIf { it.isNotEmpty() }?.let { muscles ->
                ExerciseDetailTextField(title = "Primary Muscles", value = muscles.joinToString())
            } ?: ExerciseDetailTextField(title = "Primary Muscles", value = "N/A")

            exercise.secondaryMuscles?.takeIf { it.isNotEmpty() }?.let { muscles ->
                ExerciseDetailTextField(title = "Secondary Muscles", value = muscles.joinToString())
            } ?: ExerciseDetailTextField(title = "Secondary Muscles", value = "N/A")

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                pageSpacing = 16.dp
            ) { page ->
                val imageUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/${exercise.images[page]}"
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .size(Size.ORIGINAL)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            exercise.instructions?.takeIf { it.isNotEmpty() }?.let { instructions ->
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    instructions.forEachIndexed { index, instruction ->
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = if (index == instructions.size - 1) 0.dp else 16.dp)
                        )
                    }
                }
            } ?: Text(
                text = "Instructions: N/A",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (addBool) {
                    Button(onClick = {
                        val gson = Gson()
                        val exerciseJson = gson.toJson(StrengthExercise(
                            name = exercise.name,
                            sets = 0,
                            reps = 0,
                            weight = 0
                        ))
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedExercise", exerciseJson)
                        navController.popBackStack()
                    }) {
                        Text("Add Exercise")
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailTextField(title: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}