package com.example.game.model

data class QuizCategory(
    val id: String,
    val title: String,
    val subtitle: String = "NSMQ",
    val totalQuestions: Int,
    val completedQuestions: Int = 0,
    val progressPercentage: Int = 0,
    val bannerRes: Int? = null,
    val iconRes: Int,
    val ringColorHex: Long = 0xFF7366FF
)

data class QuizQuestion(
    val id: String,
    val questionNumber: Int,
    val totalQuestions: Int,
    val subject: String = "NSMQ",
    val prompt: String,
    val imageRes: Int,
    val options: List<String>,
    val correctOptionIndex: Int,
    val coinReward: Int = 3
)

data class UserProfile(
    val name: String = "Contestant",
    val school: String = "PRESEC Legon",
    val schoolId: String = "presec",
    val schoolMoniker: String = "Blue Magicians 🦁",
    val coins: Int = 602
)

data class QuizUiState(
    val categoryId: String = "",
    val categoryTitle: String = "",
    val question: QuizQuestion? = null,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val timeRemainingSeconds: Int = 15,
    val totalTimerSeconds: Int = 15,
    val isUntimed: Boolean = false,
    val userCoins: Int = 602,
    val sessionCoinsEarned: Int = 0,
    val isFinished: Boolean = false
)
