package com.example.fitfast.model

data class StrengthExercise(
    val name: String,
    val reps: Int,
    val sets: Int,
    val weight: Int
)

data class StrengthRoutine(
    val id: Int,
    val name: String,
    val exercises: List<StrengthExercise>
)