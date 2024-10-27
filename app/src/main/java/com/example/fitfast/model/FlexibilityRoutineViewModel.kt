package com.example.fitfast.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitfast.utils.FileHelper
import kotlinx.coroutines.launch

data class WorkoutHistoryEntry(val routineName: String, val completedAt: Long)

class FlexibilityRoutineViewModel(application: Application) : AndroidViewModel(application) {
    private val _allRoutines = MutableLiveData<List<FlexibilityRoutine>>()
    val allRoutines: LiveData<List<FlexibilityRoutine>> = _allRoutines

    private val _workoutHistory = MutableLiveData<List<WorkoutHistoryEntry>>()
    val workoutHistory: LiveData<List<WorkoutHistoryEntry>> = _workoutHistory

    init {
        loadRoutines()
        loadWorkoutHistory()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
        val routines = FileHelper.loadRoutines(getApplication())
        _allRoutines.value = routines
        }
    }

    private fun loadWorkoutHistory() {
        viewModelScope.launch {
        val history = FileHelper.loadWorkoutHistory(getApplication())
        _workoutHistory.value = history
        }
    }

    fun addRoutine(routine: FlexibilityRoutine) {
        viewModelScope.launch {
            val currentRoutines = _allRoutines.value?.toMutableList() ?: mutableListOf()
            val newRoutine = routine.copy(id = (currentRoutines.maxOfOrNull { it.id } ?: 0) + 1)
            currentRoutines.add(newRoutine)
            _allRoutines.value = currentRoutines
            FileHelper.saveFlexibilityRoutines(getApplication(), currentRoutines)
        }
    }

    fun deleteRoutine(routine: FlexibilityRoutine) {
        viewModelScope.launch {
        val currentRoutines = _allRoutines.value?.toMutableList() ?: mutableListOf()
        currentRoutines.remove(routine)
        _allRoutines.value = currentRoutines
        FileHelper.saveFlexibilityRoutines(getApplication(), currentRoutines)
    }
    }

    fun addWorkoutHistory(routineName: String, completedAt: Long) {
        viewModelScope.launch {
        val currentHistory = _workoutHistory.value?.toMutableList() ?: mutableListOf()
        currentHistory.add(WorkoutHistoryEntry(routineName, completedAt))
        _workoutHistory.value = currentHistory
        FileHelper.saveWorkoutHistory(getApplication(), currentHistory)
    }
    }
}