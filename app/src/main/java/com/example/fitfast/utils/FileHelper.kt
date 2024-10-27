package com.example.fitfast.utils

import android.content.Context
import com.example.fitfast.model.FlexibilityRoutine
import com.example.fitfast.model.StrengthRoutine
import com.example.fitfast.model.WorkoutHistoryEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileHelper {
    private const val FLEXIBILITY_FILE_NAME = "flexibility_routines.json"
    private const val HISTORY_FILE_NAME = "workout_history.json"
    private const val STRENGTH_FILE_NAME = "strength_routines.json"
    private const val STRENGTH_HISTORY_FILE_NAME = "strength_workout_history.json"


    private val gson = Gson()

    fun saveFlexibilityRoutines(context: Context, routines: List<FlexibilityRoutine>) {
        val file = File(context.filesDir, FLEXIBILITY_FILE_NAME)
        val jsonString = gson.toJson(routines)
        FileWriter(file).use { it.write(jsonString) }
    }

    fun loadRoutines(context: Context): List<FlexibilityRoutine> {
        val file = File(context.filesDir, FLEXIBILITY_FILE_NAME)
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<FlexibilityRoutine>>() {}.type
        FileReader(file).use { return gson.fromJson(it, type) }
    }

    fun saveWorkoutHistory(context: Context, history: List<WorkoutHistoryEntry>) {
        val file = File(context.filesDir, HISTORY_FILE_NAME)
        val jsonString = gson.toJson(history)
        FileWriter(file).use { it.write(jsonString) }
    }

    fun loadWorkoutHistory(context: Context): List<WorkoutHistoryEntry> {
        val file = File(context.filesDir, HISTORY_FILE_NAME)
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<WorkoutHistoryEntry>>() {}.type
        FileReader(file).use { return gson.fromJson(it, type) }
    }

    fun saveStrengthRoutines(context: Context, routines: List<StrengthRoutine>) {
        val file = File(context.filesDir, STRENGTH_FILE_NAME)
        val jsonString = gson.toJson(routines)
        FileWriter(file).use { it.write(jsonString) }
    }

    fun loadStrengthRoutines(context: Context): List<StrengthRoutine> {
        val file = File(context.filesDir, STRENGTH_FILE_NAME)
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<StrengthRoutine>>() {}.type
        FileReader(file).use { return gson.fromJson(it, type) }
    }

    fun saveStrengthWorkoutHistory(context: Context, history: List<StrengthRoutine>) {
        val file = File(context.filesDir, STRENGTH_HISTORY_FILE_NAME)
        val jsonString = gson.toJson(history)
        FileWriter(file).use { it.write(jsonString) }
    }

    fun loadStrengthWorkoutHistory(context: Context): List<StrengthRoutine> {
        val file = File(context.filesDir, STRENGTH_HISTORY_FILE_NAME)
        if (!file.exists()) return emptyList()
        val type = object : TypeToken<List<StrengthRoutine>>() {}.type
        FileReader(file).use { return gson.fromJson(it, type) }
    }
}