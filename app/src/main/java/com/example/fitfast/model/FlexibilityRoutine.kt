package com.example.fitfast.model

data class FlexibilityExercise(
    val name: String,
    val duration: Int
)

data class FlexibilityRoutine(
    val id: Int,
    val name: String,
    val restTime: Int,
    val exercises: List<FlexibilityExercise>
)