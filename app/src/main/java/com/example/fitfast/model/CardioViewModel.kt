package com.example.fitfast.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CardioViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    private val _totalCaloriesBurnedThisWeek = MutableLiveData<Int>()
    val totalCaloriesBurnedThisWeek: LiveData<Int> get() = _totalCaloriesBurnedThisWeek

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private var totalCalories = 0

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val weight = sharedPreferences.getFloat("weight", -1f)
            val age = sharedPreferences.getInt("age", -1)
            val gender = sharedPreferences.getString("gender", null)

            if (weight != -1f && age != -1 && gender != null) {
                _userData.postValue(UserData(weight.toDouble(), age, gender))
            } else {
                _userData.postValue(null)
            }
        }
    }

    fun saveUserData(userData: UserData) {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putFloat("weight", userData.weight.toFloat())
                putInt("age", userData.age)
                putString("gender", userData.gender)
                apply()
            }
            _userData.postValue(userData)
        }
    }

    fun saveLISSWorkout(durationInSeconds: Int) {
        val user = _userData.value ?: return
        val caloriesBurned = calculateCaloriesBurned(durationInSeconds, user)
        totalCalories += caloriesBurned
        _totalCaloriesBurnedThisWeek.value = totalCalories
    }

    fun saveHIITWorkout(durationInSeconds: Int) {
        val user = _userData.value ?: return
        val caloriesBurned = calculateCaloriesBurned(durationInSeconds, user)
        totalCalories += caloriesBurned
        _totalCaloriesBurnedThisWeek.value = totalCalories
    }

    private fun calculateCaloriesBurned(durationInSeconds: Int, user: UserData): Int {
        val durationInMinutes = durationInSeconds / 60
        val heartRate = 120

        return if (user.gender == "Male") {
            (durationInMinutes * (0.6309 * heartRate + 0.1988 * user.weight + 0.2017 * user.age - 55.0969) / 4.184).toInt()
        } else {
            (durationInMinutes * (0.4472 * heartRate - 0.1263 * user.weight + 0.074 * user.age - 20.4022) / 4.184).toInt()
        }
    }
}

data class UserData(val weight: Double, val age: Int, val gender: String)
