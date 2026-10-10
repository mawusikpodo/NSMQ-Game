package com.example.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.R
import com.example.game.model.GameSettings
import com.example.game.model.TimerOption
import com.example.game.theme.CoinPillPurple
import com.example.game.theme.TextDarkNavy
import com.example.game.theme.TextMuted

import com.example.game.model.UserProfile

@Composable
fun SettingsDialog(
    currentSettings: GameSettings,
    userProfile: UserProfile? = null,
    onSaveSettings: (Boolean, TimerOption) -> Unit,
    onChangeSchool: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(currentSettings.soundEnabled) }
    var selectedTimer by remember { mutableStateOf(currentSettings.timerOption) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Game Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )

                if (userProfile != null && onChangeSchool != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF3F0FF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChangeSchool() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Your SHS School",
                                    fontSize = 11.sp,
                                    color = CoinPillPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${userProfile.school} (${userProfile.schoolMoniker})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkNavy
                                )
                            }
                            Text(
                                text = "Change 🎓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoinPillPurple
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Setting Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F9FE))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                id = if (soundEnabled) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                            ),
                            contentDescription = "Sound",
                            tint = CoinPillPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sound Effects",
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkNavy,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (soundEnabled) "On (Chimes & coins)" else "Muted",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CoinPillPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Timer Options
                Text(
                    text = "Question Timer Speed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDarkNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    TimerOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTimer = option }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTimer == option,
                                onClick = { selectedTimer = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CoinPillPurple
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.displayName,
                                fontSize = 14.sp,
                                color = if (selectedTimer == option) TextDarkNavy else TextMuted,
                                fontWeight = if (selectedTimer == option) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onSaveSettings(soundEnabled, selectedTimer)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoinPillPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save & Play", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
