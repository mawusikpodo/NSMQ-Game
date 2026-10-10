package com.example.game.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.audio.SoundPlayer
import com.example.game.data.QuizRepository
import com.example.game.model.QuizQuestion
import com.example.game.model.QuizUiState
import com.example.game.model.TimerOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val soundManager: SoundPlayer,
    private val categoryId: String
) : ViewModel() {

    private val questions: List<QuizQuestion> = repository.getQuestionsForCategory(categoryId)
    private var currentQuestionIndex = 0

    private val timerSetting = repository.settings.value.timerOption
    private val isUntimed = timerSetting == TimerOption.UNTIMED
    private val initialSeconds = if (isUntimed) 0 else timerSetting.seconds

    private val _uiState = MutableStateFlow(
        QuizUiState(
            categoryId = categoryId,
            question = questions.getOrNull(0),
            questionIndex = 0,
            totalQuestions = questions.size,
            timeRemainingSeconds = initialSeconds,
            totalTimerSeconds = initialSeconds,
            isUntimed = isUntimed,
            userCoins = repository.userProfile.value.coins
        )
    )
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Sync sound settings
        soundManager.soundEnabled = repository.settings.value.soundEnabled
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        if (isUntimed) return

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0 && !_uiState.value.isAnswered) {
                delay(1000)
                val newRemaining = _uiState.value.timeRemainingSeconds - 1
                if (newRemaining in 1..3) {
                    soundManager.playCountdownTick()
                }
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = newRemaining)
            }

            if (_uiState.value.timeRemainingSeconds <= 0 && !_uiState.value.isAnswered) {
                // Time up! Auto-mark as answered incorrectly
                handleTimeExpired()
            }
        }
    }

    private fun handleTimeExpired() {
        soundManager.playIncorrectSound()
        _uiState.value = _uiState.value.copy(
            isAnswered = true,
            isCorrect = false,
            selectedOptionIndex = -1
        )
    }

    fun onOptionSelected(index: Int) {
        val currentState = _uiState.value
        if (currentState.isAnswered) return
        val currentQ = currentState.question ?: return

        timerJob?.cancel()
        val isCorrect = index == currentQ.correctOptionIndex

        if (isCorrect) {
            soundManager.playCorrectSound()
            soundManager.playCoinSound()
            repository.addCoins(currentQ.coinReward)
            val updatedCoins = currentState.userCoins + currentQ.coinReward
            _uiState.value = currentState.copy(
                isAnswered = true,
                isCorrect = true,
                selectedOptionIndex = index,
                userCoins = updatedCoins,
                sessionCoinsEarned = currentState.sessionCoinsEarned + currentQ.coinReward
            )
        } else {
            soundManager.playIncorrectSound()
            _uiState.value = currentState.copy(
                isAnswered = true,
                isCorrect = false,
                selectedOptionIndex = index
            )
        }
    }

    fun onNextQuestion() {
        soundManager.playClickSound()
        if (currentQuestionIndex + 1 < questions.size) {
            currentQuestionIndex++
            val nextQuestion = questions[currentQuestionIndex]
            _uiState.value = _uiState.value.copy(
                question = nextQuestion,
                questionIndex = currentQuestionIndex,
                selectedOptionIndex = null,
                isAnswered = false,
                isCorrect = false,
                timeRemainingSeconds = initialSeconds
            )
            startTimer()
        } else {
            _uiState.value = _uiState.value.copy(isFinished = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
