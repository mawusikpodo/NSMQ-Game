package com.example.game.data

import android.content.Context
import android.content.SharedPreferences
import com.example.game.R
import com.example.game.model.GameSettings
import com.example.game.model.QuizCategory
import com.example.game.model.QuizQuestion
import com.example.game.model.TimerOption
import com.example.game.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

interface QuizRepository {
    val userProfile: StateFlow<UserProfile>
    val settings: StateFlow<GameSettings>
    fun updateSettings(soundEnabled: Boolean, timerOption: TimerOption)
    fun updateUserProfile(name: String, schoolId: String, schoolName: String, schoolMoniker: String)
    fun addCoins(amount: Int)
    fun getFeaturedCategories(): List<QuizCategory>
    fun getUnfinishedCategories(): List<QuizCategory>
    fun getRoundsCategories(): List<QuizCategory>
    fun getQuestionsForCategory(categoryId: String): List<QuizQuestion>
}

class QuizDataRepository(private val context: Context) : QuizRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("kids_quiz_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = prefs.getString("user_name", "Contestant") ?: "Contestant",
            school = prefs.getString("user_school", "PRESEC Legon") ?: "PRESEC Legon",
            schoolId = prefs.getString("user_school_id", "presec") ?: "presec",
            schoolMoniker = prefs.getString("user_school_moniker", "Blue Magicians 🦁") ?: "Blue Magicians 🦁",
            coins = prefs.getInt("user_coins", 602)
        )
    )
    override val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _settings = MutableStateFlow(
        GameSettings(
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            timerOption = TimerOption.fromSeconds(prefs.getInt("timer_seconds", 15))
        )
    )
    override val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val cachedQuestions = mutableListOf<QuizQuestion>()

    init {
        loadQuestionsFromAssets()
    }

    private fun loadQuestionsFromAssets() {
        try {
            val jsonText = context.assets.open("nsmq_questions.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonText)
            cachedQuestions.clear()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val category = obj.getString("category")
                val subject = obj.optString("subject", "NSMQ")
                val prompt = obj.getString("prompt")
                val points = obj.optInt("points", 3)
                val correctIndex = obj.getInt("correctOptionIndex")
                val imageName = obj.optString("imageName", "icon_science")

                val optionsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }

                val imageRes = when (imageName) {
                    "banner_sports" -> R.drawable.banner_sports
                    "banner_science" -> R.drawable.banner_science
                    "icon_math" -> R.drawable.icon_math
                    "icon_animals" -> R.drawable.icon_animals
                    else -> R.drawable.icon_science
                }

                cachedQuestions.add(
                    QuizQuestion(
                        id = id,
                        questionNumber = i + 1,
                        totalQuestions = jsonArray.length(),
                        subject = "$subject ($category)",
                        prompt = prompt,
                        imageRes = imageRes,
                        options = options,
                        correctOptionIndex = correctIndex,
                        coinReward = points
                    )
                )
            }
        } catch (_: Exception) {
            // Fallback handled below
        }
    }

    override fun updateSettings(soundEnabled: Boolean, timerOption: TimerOption) {
        prefs.edit()
            .putBoolean("sound_enabled", soundEnabled)
            .putInt("timer_seconds", timerOption.seconds)
            .apply()
        _settings.value = GameSettings(soundEnabled, timerOption)
    }

    override fun updateUserProfile(name: String, schoolId: String, schoolName: String, schoolMoniker: String) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_school", schoolName)
            .putString("user_school_id", schoolId)
            .putString("user_school_moniker", schoolMoniker)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            name = name,
            school = schoolName,
            schoolId = schoolId,
            schoolMoniker = schoolMoniker
        )
    }

    override fun addCoins(amount: Int) {
        val currentCoins = _userProfile.value.coins
        val updated = currentCoins + amount
        prefs.edit().putInt("user_coins", updated).apply()
        _userProfile.value = _userProfile.value.copy(coins = updated)
    }

    override fun getFeaturedCategories(): List<QuizCategory> = listOf(
        QuizCategory(
            id = "speed_race",
            title = "Speed Race",
            subtitle = "Round 2 • Rapid Fire",
            totalQuestions = 15,
            completedQuestions = 5,
            progressPercentage = 33,
            bannerRes = R.drawable.banner_sports,
            iconRes = R.drawable.banner_sports,
            ringColorHex = 0xFF7366FF
        ),
        QuizCategory(
            id = "problem_of_the_day",
            title = "Problem of the Day",
            subtitle = "Round 3 • 10 Points",
            totalQuestions = 5,
            completedQuestions = 2,
            progressPercentage = 40,
            bannerRes = R.drawable.banner_science,
            iconRes = R.drawable.banner_science,
            ringColorHex = 0xFFF59E0B
        ),
        QuizCategory(
            id = "riddles",
            title = "NSMQ Riddles",
            subtitle = "Round 5 • 5 Points",
            totalQuestions = 10,
            completedQuestions = 6,
            progressPercentage = 60,
            bannerRes = R.drawable.banner_science,
            iconRes = R.drawable.banner_science,
            ringColorHex = 0xFF06B6D4
        )
    )

    override fun getUnfinishedCategories(): List<QuizCategory> = listOf(
        QuizCategory(
            id = "physics",
            title = "Physics",
            subtitle = "Mechanics & Waves",
            totalQuestions = 20,
            completedQuestions = 12,
            progressPercentage = 60,
            iconRes = R.drawable.icon_science,
            ringColorHex = 0xFF8B5CF6
        ),
        QuizCategory(
            id = "chemistry",
            title = "Chemistry",
            subtitle = "Organic & Stoichiometry",
            totalQuestions = 20,
            completedQuestions = 8,
            progressPercentage = 40,
            iconRes = R.drawable.icon_science,
            ringColorHex = 0xFF06B6D4
        ),
        QuizCategory(
            id = "biology",
            title = "Biology",
            subtitle = "Genetics & Physiology",
            totalQuestions = 20,
            completedQuestions = 12,
            progressPercentage = 60,
            iconRes = R.drawable.icon_animals,
            ringColorHex = 0xFF10B981
        ),
        QuizCategory(
            id = "math",
            title = "Elective Mathematics",
            subtitle = "Calculus & Algebra",
            totalQuestions = 20,
            completedQuestions = 8,
            progressPercentage = 40,
            iconRes = R.drawable.icon_math,
            ringColorHex = 0xFFF97316
        ),
        QuizCategory(
            id = "true_false",
            title = "True or False",
            subtitle = "Round 4 • Rapid Evaluation",
            totalQuestions = 15,
            completedQuestions = 6,
            progressPercentage = 40,
            iconRes = R.drawable.icon_science,
            ringColorHex = 0xFFEC4899
        ),
        QuizCategory(
            id = "problem_of_the_day",
            title = "Problem of the Day",
            subtitle = "Round 3 • Deep Derivation",
            totalQuestions = 5,
            completedQuestions = 2,
            progressPercentage = 40,
            iconRes = R.drawable.icon_math,
            ringColorHex = 0xFFF59E0B
        )
    )

    override fun getRoundsCategories(): List<QuizCategory> = listOf(
        QuizCategory(
            id = "physics",
            title = "Round 1: Fundamentals",
            subtitle = "Major Subject Questions",
            totalQuestions = 20,
            completedQuestions = 10,
            progressPercentage = 50,
            iconRes = R.drawable.icon_science,
            ringColorHex = 0xFF8B5CF6
        ),
        QuizCategory(
            id = "speed_race",
            title = "Round 2: Speed Race",
            subtitle = "Speed & Accuracy (Bell Ring)",
            totalQuestions = 15,
            completedQuestions = 5,
            progressPercentage = 33,
            iconRes = R.drawable.banner_sports,
            ringColorHex = 0xFF7366FF
        ),
        QuizCategory(
            id = "problem_of_the_day",
            title = "Round 3: Problem of the Day",
            subtitle = "10 Marks Comprehensive Problem",
            totalQuestions = 5,
            completedQuestions = 2,
            progressPercentage = 40,
            iconRes = R.drawable.icon_math,
            ringColorHex = 0xFFF59E0B
        ),
        QuizCategory(
            id = "true_false",
            title = "Round 4: True or False",
            subtitle = "+2 for correct, -1 for penalty",
            totalQuestions = 15,
            completedQuestions = 6,
            progressPercentage = 40,
            iconRes = R.drawable.icon_science,
            ringColorHex = 0xFFEC4899
        ),
        QuizCategory(
            id = "riddles",
            title = "Round 5: NSMQ Riddles",
            subtitle = "4 Clues • 5, 4, 3 Points",
            totalQuestions = 10,
            completedQuestions = 6,
            progressPercentage = 60,
            iconRes = R.drawable.banner_science,
            ringColorHex = 0xFF06B6D4
        )
    )

    override fun getQuestionsForCategory(categoryId: String): List<QuizQuestion> {
        val filtered = cachedQuestions.filter { q ->
            when (categoryId.lowercase()) {
                "speed_race" -> q.id.startsWith("speed") || q.subject.contains("Speed", ignoreCase = true)
                "riddles" -> q.id.startsWith("riddle") || q.subject.contains("Riddle", ignoreCase = true)
                "problem_of_the_day" -> q.id.startsWith("pod") || q.subject.contains("Problem of the Day", ignoreCase = true)
                "true_false" -> q.id.startsWith("tf") || q.subject.contains("True or False", ignoreCase = true)
                "physics" -> q.id.startsWith("phys") || q.subject.contains("Physics", ignoreCase = true)
                "chemistry" -> q.id.startsWith("chem") || q.subject.contains("Chemistry", ignoreCase = true)
                "biology" -> q.id.startsWith("bio") || q.subject.contains("Biology", ignoreCase = true)
                "math" -> q.id.startsWith("math") || q.subject.contains("Math", ignoreCase = true)
                else -> q.subject.contains(categoryId, ignoreCase = true) || q.id.startsWith(categoryId.take(3))
            }
        }

        val resultList = if (filtered.isNotEmpty()) filtered else cachedQuestions

        // Re-index question numbers for this session
        return resultList.mapIndexed { index, q ->
            q.copy(
                questionNumber = index + 1,
                totalQuestions = resultList.size
            )
        }
    }
}
