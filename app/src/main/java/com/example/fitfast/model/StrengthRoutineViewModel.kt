package com.example.fitfast.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitfast.utils.FileHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException

class StrengthRoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val _allRoutines = MutableLiveData<List<StrengthRoutine>>()
    val allRoutines: LiveData<List<StrengthRoutine>> = _allRoutines

    private val _searchResults = MutableLiveData<List<JsonExercise>>(emptyList())
    val searchResults: LiveData<List<JsonExercise>> = _searchResults

    private val _exerciseDetail = MutableLiveData<JsonExercise>()
    val exerciseDetail: LiveData<JsonExercise> = _exerciseDetail

    private val _allExercises = MutableLiveData<List<JsonExercise>>()
    val allExercises: LiveData<List<JsonExercise>> = _allExercises

    private val _workoutHistory = MutableLiveData<List<StrengthRoutine>>()
    val workoutHistory: LiveData<List<StrengthRoutine>> = _workoutHistory

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError


    init {
        loadRoutines()
        loadExercises()
        loadWorkoutHistory()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            val routines = FileHelper.loadStrengthRoutines(getApplication())
            _allRoutines.value = routines
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            try {
                val exercises = fetchExercisesFromGitHub()
                _allExercises.value = exercises
                _networkError.value = false
            } catch (e: IOException) {
                _networkError.value = true
            } catch (e: UnknownHostException) {
                _networkError.value = true
            }
        }
    }

    private fun loadWorkoutHistory() {
        viewModelScope.launch {
            val history = FileHelper.loadStrengthWorkoutHistory(getApplication())
            _workoutHistory.value = history
        }
    }

    private suspend fun fetchExercisesFromGitHub(): List<JsonExercise> {
        return withContext(Dispatchers.IO) {
            val url = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/exercises.json"
            val json = URL(url).readText()
            val type = object : TypeToken<List<JsonExercise>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    fun searchExercises(query: String): LiveData<List<JsonExercise>> {
        val filteredExercises = _allExercises.value?.filter { it.name.contains(query, ignoreCase = true) }
        return MutableLiveData(filteredExercises ?: emptyList())
    }

    fun getExerciseDetail(exerciseId: String) {
        _exerciseDetail.value = _allExercises.value?.find { it.id == exerciseId }
    }

    fun addRoutine(routine: StrengthRoutine) {
        val currentRoutines = _allRoutines.value?.toMutableList() ?: mutableListOf()
        currentRoutines.add(routine)
        _allRoutines.value = currentRoutines
        FileHelper.saveStrengthRoutines(getApplication(), currentRoutines)
    }

    fun deleteRoutine(routine: StrengthRoutine) {
        val currentRoutines = _allRoutines.value?.toMutableList() ?: mutableListOf()
        currentRoutines.remove(routine)
        _allRoutines.value = currentRoutines
        FileHelper.saveStrengthRoutines(getApplication(), currentRoutines)
    }

    fun getRoutine(id: Int): LiveData<StrengthRoutine> {
        return MutableLiveData(_allRoutines.value?.find { it.id == id })
    }

    fun saveCompletedWorkout(routine: StrengthRoutine, completedSets: Map<String, Boolean>, weights: Map<String, String>, reps: Map<String, String>) {
        viewModelScope.launch {
            val updatedExercises = routine.exercises.map { exercise ->
                val updatedSets = (0 until exercise.sets).map { setIndex ->
                    val setKey = "${exercise.name}-$setIndex"
                    if (completedSets[setKey] == true) {
                        StrengthExercise(
                            name = exercise.name,
                            sets = exercise.sets,
                            reps = reps[setKey]?.toIntOrNull() ?: exercise.reps,
                            weight = weights[setKey]?.toIntOrNull() ?: exercise.weight
                        )
                    } else {
                        exercise
                    }
                }
                updatedSets
            }.flatten()
            val completedRoutine = StrengthRoutine(
                id = routine.id,
                name = routine.name,
                exercises = updatedExercises
            )
            val currentHistory = _workoutHistory.value?.toMutableList() ?: mutableListOf()
            currentHistory.add(completedRoutine)
            _workoutHistory.value = currentHistory
            FileHelper.saveStrengthWorkoutHistory(getApplication(), currentHistory)
        }
    }

    fun getLastCompletedWorkout(routineId: Int): StrengthRoutine? {
        return _workoutHistory.value?.findLast { it.id == routineId }
    }

    fun getLastCompletedSetValues(routineId: Int, exerciseName: String): Map<String, Pair<Int, Int>> {
        val history = _workoutHistory.value?.filter { it.id == routineId } ?: return emptyMap()
        val lastCompletedValues = mutableMapOf<String, Pair<Int, Int>>()

        for (workout in history.reversed()) {
            workout.exercises.forEachIndexed { index, exercise ->
                if (exercise.name == exerciseName) {
                    val setKey = "$exerciseName-$index"
                    if (!lastCompletedValues.containsKey(setKey) && exercise.weight > 0 && exercise.reps > 0) {
                        lastCompletedValues[setKey] = Pair(exercise.weight, exercise.reps)
                    }
                }
            }
            if (lastCompletedValues.size == workout.exercises.size) break
        }

        return lastCompletedValues
    }
}

    /*
    fun searchExercises(query: String) = liveData {
        val response = ExerciseApiService.create().searchExercises(query)
        if (response.isSuccessful) {
            emit(response.body() ?: emptyList())
        } else {
            emit(emptyList())
        }
    }

    fun getAllExercises(): LiveData<List<ApiExercise>> = liveData {
        val response = ExerciseApiService.create().getAllExercises(limit = 1500)
        if (response.isSuccessful) {
            emit(response.body() ?: emptyList())
        } else {
            emit(emptyList())
        }
    }

    fun getExerciseDetail(exerciseId: String) = liveData {
        val response = ExerciseApiService.create().getExerciseDetail(exerciseId)
        if (response.isSuccessful) {
            emit(response.body())
        } else {
            emit(null)
        }
    }
    */
