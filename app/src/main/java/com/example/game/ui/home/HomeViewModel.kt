package com.example.game.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.data.QuizRepository
import com.example.game.model.GameSettings
import com.example.game.model.QuizCategory
import com.example.game.model.TimerOption
import com.example.game.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: QuizRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val settings: StateFlow<GameSettings> = repository.settings

    val featuredCategories: List<QuizCategory> = repository.getFeaturedCategories()
    val unfinishedCategories: List<QuizCategory> = repository.getUnfinishedCategories()
    val roundsCategories: List<QuizCategory> = repository.getRoundsCategories()

    fun updateSettings(soundEnabled: Boolean, timerOption: TimerOption) {
        repository.updateSettings(soundEnabled, timerOption)
    }

    fun selectSchool(school: com.example.game.model.School) {
        val currentName = userProfile.value.name
        repository.updateUserProfile(
            name = currentName,
            schoolId = school.id,
            schoolName = school.shortName,
            schoolMoniker = school.moniker
        )
    }

    fun updateName(name: String) {
        val currentProfile = userProfile.value
        repository.updateUserProfile(
            name = name,
            schoolId = currentProfile.schoolId,
            schoolName = currentProfile.school,
            schoolMoniker = currentProfile.schoolMoniker
        )
    }
}
