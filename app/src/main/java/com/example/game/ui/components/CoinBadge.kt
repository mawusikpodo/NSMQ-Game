package com.example.game.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.R
import com.example.game.theme.CoinPillPurple

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(CoinPillPurple)
            .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = coins,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> -height } togetherWith slideOutVertically { height -> height }
                    } else {
                        slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
                    }
                },
                label = "CoinCounterAnimation"
            ) { targetCount ->
                Text(
                    text = targetCount.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            // Coin graphic pill
            Image(
                painter = painterResource(id = R.drawable.coin_3d),
                contentDescription = "Coins",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        }
    }
}
