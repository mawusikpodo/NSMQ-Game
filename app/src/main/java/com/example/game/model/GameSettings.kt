package com.example.game.model

enum class TimerOption(val seconds: Int, val displayName: String) {
    FAST(10, "10 Seconds (Fast)"),
    STANDARD(15, "15 Seconds (Standard)"),
    RELAXED(30, "30 Seconds (Relaxed)"),
    UNTIMED(0, "Untimed (No Rush)");

    companion object {
        fun fromSeconds(sec: Int): TimerOption {
            return entries.find { it.seconds == sec } ?: STANDARD
        }
    }
}

data class GameSettings(
    val soundEnabled: Boolean = true,
    val timerOption: TimerOption = TimerOption.STANDARD
)
