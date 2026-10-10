package com.example.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.game.audio.SoundManager
import com.example.game.data.QuizDataRepository
import com.example.game.ui.home.HomeScreen
import com.example.game.ui.home.HomeViewModel
import com.example.game.ui.quiz.QuizScreen
import com.example.game.ui.quiz.QuizViewModel

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val repository = remember { QuizDataRepository(context) }
    val soundManager = remember { SoundManager(context) }
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                val homeViewModel = viewModel { HomeViewModel(repository) }
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartQuiz = { categoryId ->
                        backStack.add(QuizRoute(categoryId))
                    }
                )
            }
            entry<QuizRoute> { route ->
                val quizViewModel = viewModel(key = route.categoryId) {
                    QuizViewModel(repository, soundManager, route.categoryId)
                }
                QuizScreen(
                    viewModel = quizViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
