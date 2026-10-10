package com.example.game.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.R
import com.example.game.theme.AnswerCorrectGreen
import com.example.game.theme.AnswerIncorrectRed
import com.example.game.theme.CardBackgroundWhite
import com.example.game.theme.TextDarkNavy

enum class OptionButtonState {
    DEFAULT,
    CORRECT,
    INCORRECT,
    DISABLED
}

@Composable
fun QuizOptionButton(
    text: String,
    state: OptionButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            OptionButtonState.DEFAULT -> CardBackgroundWhite
            OptionButtonState.CORRECT -> CardBackgroundWhite
            OptionButtonState.INCORRECT -> Color(0xFFFFEBEE)
            OptionButtonState.DISABLED -> Color.White.copy(alpha = 0.5f)
        },
        label = "ButtonBgColor"
    )

    val textColor by animateColorAsState(
        targetValue = when (state) {
            OptionButtonState.DEFAULT -> TextDarkNavy
            OptionButtonState.CORRECT -> AnswerCorrectGreen
            OptionButtonState.INCORRECT -> AnswerIncorrectRed
            OptionButtonState.DISABLED -> TextDarkNavy.copy(alpha = 0.4f)
        },
        label = "ButtonTextColor"
    )

    val isClickable = state == OptionButtonState.DEFAULT

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = if (state == OptionButtonState.CORRECT) 10.dp else 6.dp,
                shape = RoundedCornerShape(34.dp),
                spotColor = Color(0x33000000)
            )
            .clip(RoundedCornerShape(34.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (isClickable) ripple() else null,
                enabled = isClickable,
                onClick = onClick
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state == OptionButtonState.CORRECT) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = "Correct",
                    modifier = Modifier.size(32.dp)
                )
            } else {
                // Invisible spacer to keep text centered if needed
                Box(modifier = Modifier.size(32.dp))
            }

            Text(
                text = text.uppercase(),
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // Balance spacer on right
            Box(modifier = Modifier.size(32.dp))
        }
    }
}
