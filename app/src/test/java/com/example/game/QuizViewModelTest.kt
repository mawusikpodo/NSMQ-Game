package com.example.game

import com.example.game.audio.SoundPlayer
import com.example.game.data.QuizRepository
import com.example.game.model.GameSettings
import com.example.game.model.QuizCategory
import com.example.game.model.QuizQuestion
import com.example.game.model.TimerOption
import com.example.game.model.UserProfile
import com.example.game.ui.quiz.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQuizRepository
    private lateinit var fakeSoundPlayer: FakeSoundPlayer

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQuizRepository()
        fakeSoundPlayer = FakeSoundPlayer()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialQuizState_loadsFirstQuestionCorrectly() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        val state = viewModel.uiState.value

        assertEquals("sport_quiz", state.categoryId)
        assertEquals(2, state.totalQuestions)
        assertEquals("What is the name of the toy cowboy in Toy Story?", state.question?.prompt)
        assertFalse(state.isAnswered)
        assertFalse(state.isCorrect)
        assertEquals(602, state.userCoins)
    }

    @Test
    fun onOptionSelected_correctAnswer_updatesStateAwardsCoinsAndPlaysAudio() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        // Question 0 correct answer index is 1 ("WOODY")
        viewModel.onOptionSelected(1)

        val state = viewModel.uiState.value
        assertTrue(state.isAnswered)
        assertTrue(state.isCorrect)
        assertEquals(1, state.selectedOptionIndex)
        assertEquals(612, state.userCoins)
        assertEquals(10, state.sessionCoinsEarned)

        assertTrue(fakeSoundPlayer.correctSoundPlayed)
        assertTrue(fakeSoundPlayer.coinSoundPlayed)
        assertFalse(fakeSoundPlayer.incorrectSoundPlayed)
    }

    @Test
    fun onOptionSelected_incorrectAnswer_updatesStateNoCoins() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        // Question 0 incorrect answer index is 0 ("SMITH")
        viewModel.onOptionSelected(0)

        val state = viewModel.uiState.value
        assertTrue(state.isAnswered)
        assertFalse(state.isCorrect)
        assertEquals(0, state.selectedOptionIndex)
        assertEquals(602, state.userCoins)
        assertEquals(0, state.sessionCoinsEarned)

        assertFalse(fakeSoundPlayer.correctSoundPlayed)
        assertTrue(fakeSoundPlayer.incorrectSoundPlayed)
    }

    @Test
    fun onOptionSelected_subsequentClicksIgnored() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        viewModel.onOptionSelected(1) // Correct
        viewModel.onOptionSelected(0) // Attempt to change

        val state = viewModel.uiState.value
        assertTrue(state.isCorrect)
        assertEquals(1, state.selectedOptionIndex)
        assertEquals(612, state.userCoins)
    }

    @Test
    fun onNextQuestion_advancesToNextQuestion() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        viewModel.onOptionSelected(1)
        viewModel.onNextQuestion()

        val state = viewModel.uiState.value
        assertEquals(1, state.questionIndex)
        assertEquals("What is 5 + 5?", state.question?.prompt)
        assertFalse(state.isAnswered)
        assertFalse(state.isCorrect)
        assertFalse(state.isFinished)
        assertTrue(fakeSoundPlayer.clickSoundPlayed)
    }

    @Test
    fun onNextQuestion_whenLastQuestion_marksFinished() {
        val viewModel = QuizViewModel(fakeRepository, fakeSoundPlayer, "sport_quiz")
        viewModel.onOptionSelected(1)
        viewModel.onNextQuestion() // To question 2
        viewModel.onOptionSelected(0)
        viewModel.onNextQuestion() // Finished!

        val state = viewModel.uiState.value
        assertTrue(state.isFinished)
    }

    @Test
    fun homeViewModel_selectSchool_updatesUserProfile() {
        val homeVm = com.example.game.ui.home.HomeViewModel(fakeRepository)
        val persco = com.example.game.model.SchoolCatalog.schools.first { it.id == "persco" }

        homeVm.selectSchool(persco)

        val updatedProfile = homeVm.userProfile.value
        assertEquals("PERSCO Nkwatia", updatedProfile.school)
        assertEquals("persco", updatedProfile.schoolId)
        assertEquals("The Mountain Boys 🏔️", updatedProfile.schoolMoniker)
    }

    @Test
    fun schoolCatalog_containsMajorGhanaianSchools() {
        val schools = com.example.game.model.SchoolCatalog.schools
        assertTrue(schools.any { it.shortName.contains("PRESEC") })
        assertTrue(schools.any { it.shortName.contains("Prempeh") })
        assertTrue(schools.any { it.shortName.contains("Wey Gey Hey") })
        assertTrue(schools.any { it.shortName.contains("Mfantsipim") })
    }
}

private class FakeSoundPlayer : SoundPlayer {
    override var soundEnabled: Boolean = true
    var correctSoundPlayed = false
    var coinSoundPlayed = false
    var incorrectSoundPlayed = false
    var clickSoundPlayed = false
    var countdownTickPlayed = false

    override fun playCorrectSound() { correctSoundPlayed = true }
    override fun playCoinSound() { coinSoundPlayed = true }
    override fun playIncorrectSound() { incorrectSoundPlayed = true }
    override fun playClickSound() { clickSoundPlayed = true }
    override fun playCountdownTick() { countdownTickPlayed = true }
}

private class FakeQuizRepository : QuizRepository {
    private val _userProfile = MutableStateFlow(UserProfile(name = "Moss", coins = 602))
    override val userProfile: StateFlow<UserProfile> = _userProfile

    private val _settings = MutableStateFlow(GameSettings(soundEnabled = true, timerOption = TimerOption.STANDARD))
    override val settings: StateFlow<GameSettings> = _settings

    override fun updateSettings(soundEnabled: Boolean, timerOption: TimerOption) {
        _settings.value = GameSettings(soundEnabled, timerOption)
    }

    override fun updateUserProfile(name: String, schoolId: String, schoolName: String, schoolMoniker: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            school = schoolName,
            schoolId = schoolId,
            schoolMoniker = schoolMoniker
        )
    }

    override fun addCoins(amount: Int) {
        _userProfile.value = _userProfile.value.copy(coins = _userProfile.value.coins + amount)
    }

    override fun getFeaturedCategories(): List<QuizCategory> = emptyList()
    override fun getUnfinishedCategories(): List<QuizCategory> = emptyList()
    override fun getRoundsCategories(): List<QuizCategory> = emptyList()

    override fun getQuestionsForCategory(categoryId: String): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            questionNumber = 1,
            totalQuestions = 2,
            prompt = "What is the name of the toy cowboy in Toy Story?",
            imageRes = 0,
            options = listOf("SMITH", "WOODY", "JACK"),
            correctOptionIndex = 1,
            coinReward = 10
        ),
        QuizQuestion(
            id = "q2",
            questionNumber = 2,
            totalQuestions = 2,
            prompt = "What is 5 + 5?",
            imageRes = 0,
            options = listOf("10", "12", "15"),
            correctOptionIndex = 0,
            coinReward = 10
        )
    )
}
