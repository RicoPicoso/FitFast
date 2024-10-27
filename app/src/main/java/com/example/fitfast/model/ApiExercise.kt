package com.example.fitfast.model

/*data class ApiExercise(
    val id: String,
    val name: String,
    val target: String,
    val equipment: String,
    val bodyPart: String
)*/

data class ApiExercise(
    val id: String,
    val name: String,
    val target: String,
    val equipment: String,
    val bodyPart: String,
    val gifUrl: String,
    val secondaryMuscles: List<String>,
    val instructions: List<String>
)