package com.example.game.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.R
import com.example.game.model.QuizQuestion
import com.example.game.ui.components.CoinBadge
import com.example.game.ui.components.CountdownTimerRing
import com.example.game.ui.components.OptionButtonState
import com.example.game.ui.components.QuizOptionButton
import com.example.game.theme.CoinPillPurple
import com.example.game.theme.QuizPurpleBackground
import com.example.game.theme.TextLightWhite
import com.example.game.theme.TextTranslucentWhite

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isFinished) {
        QuizCompletionDialog(
            coinsEarned = state.sessionCoinsEarned,
            onBackHome = onNavigateBack
        )
    }

    val currentQuestion = state.question ?: return

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(QuizPurpleBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit 'X' Button in translucent circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Exit Quiz",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Countdown Timer Ring
                CountdownTimerRing(
                    secondsRemaining = state.timeRemainingSeconds,
                    totalSeconds = state.totalTimerSeconds,
                    isUntimed = state.isUntimed,
                    size = 56.dp
                )

                // Coin Badge
                CoinBadge(coins = state.userCoins)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Question Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF4C3E75))
            ) {
                Image(
                    painter = painterResource(id = currentQuestion.imageRes),
                    contentDescription = currentQuestion.prompt,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Question Count
            Text(
                text = "${currentQuestion.subject} • Question ${currentQuestion.questionNumber} of ${currentQuestion.totalQuestions}",
                color = TextTranslucentWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Question Prompt
            Text(
                text = currentQuestion.prompt,
                color = TextLightWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Options List or Answered View
            if (!state.isAnswered) {
                // Active Answering Mode: Show all buttons
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    currentQuestion.options.forEachIndexed { index, optionText ->
                        QuizOptionButton(
                            text = optionText,
                            state = OptionButtonState.DEFAULT,
                            onClick = { viewModel.onOptionSelected(index) }
                        )
                    }
                }
            } else {
                // Answered State: Mirrors Answered Screen.png
                AnsweredStateView(
                    question = currentQuestion,
                    isCorrect = state.isCorrect,
                    selectedOptionIndex = state.selectedOptionIndex,
                    onNext = { viewModel.onNextQuestion() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AnsweredStateView(
    question: QuizQuestion,
    isCorrect: Boolean,
    selectedOptionIndex: Int?,
    onNext: () -> Unit
) {
    val correctIndex = question.correctOptionIndex
    val correctText = question.options.getOrNull(correctIndex) ?: ""

    // Infinite bouncing transition for the gold coin
    val infiniteTransition = rememberInfiniteTransition(label = "CoinBounce")
    val coinScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CoinScaleAnimation"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Correct Answer Option Card
        QuizOptionButton(
            text = correctText,
            state = OptionButtonState.CORRECT,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Feedback Banner
        if (isCorrect) {
            Text(
                text = "That's the right Answer - +${question.coinReward} Points",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3D Gold Coin with celebratory bounce
            Image(
                painter = painterResource(id = R.drawable.coin_3d),
                contentDescription = "Reward Coin",
                modifier = Modifier
                    .size(80.dp)
                    .scale(coinScale)
            )
        } else {
            Text(
                text = "Oops! Keep learning and try the next one!",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Next Button at bottom right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NEXT",
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Large circular action button with purple arrow
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(10.dp, CircleShape, spotColor = Color(0x33000000))
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Next Question",
                    tint = CoinPillPurple,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
