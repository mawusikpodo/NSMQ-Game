package com.example.game

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data class QuizRoute(val categoryId: String) : NavKey
