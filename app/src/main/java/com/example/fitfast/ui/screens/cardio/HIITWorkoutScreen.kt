package com.example.fitfast.ui.screens.cardio

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitfast.model.CardioViewModel
import com.example.fitfast.utils.FormatTime
import kotlinx.coroutines.delay

@Composable
fun HIITWorkoutScreen(navController: NavHostController, viewModel: CardioViewModel) {
    var elapsedTime by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    LaunchedEffect(Unit) {
        while (!isFinished) {
            delay(1000L)
            elapsedTime += 1
            if (elapsedTime % 60 == 0 || elapsedTime % 60 == 40) {
                vibrator.vibrate(500)
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
            "HIIT Workout",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Elapsed Time: ${FormatTime(elapsedTime)}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        val intensityText = if ((elapsedTime % 60) < 40) "Moderate Intensity" else "High Intensity"
        val intensityColor = if ((elapsedTime % 60) < 40) Color.Yellow else Color.Red

        Text(
            text = intensityText,
            style = MaterialTheme.typography.displaySmall,
            color = intensityColor,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    isFinished = true
                    viewModel.saveHIITWorkout(elapsedTime)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Save Workout", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    isFinished = true
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Abort", fontSize = 18.sp)
            }
        }
    }
}
