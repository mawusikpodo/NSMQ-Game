package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

interface SoundPlayer {
    var soundEnabled: Boolean
    fun playCorrectSound()
    fun playCoinSound()
    fun playIncorrectSound()
    fun playClickSound()
    fun playCountdownTick()
}

/**
 * Synthesizes kid-friendly game sound effects on-the-fly using AudioTrack.
 * Zero asset dependencies, lightweight, and guaranteed to work everywhere.
 */
class SoundManager(private val context: Context) : SoundPlayer {

    override var soundEnabled: Boolean = true

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100

    override fun playCorrectSound() {
        if (!soundEnabled) return
        audioScope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            val noteDurationMs = 90
            val totalSamples = (sampleRate * (noteDurationMs * notes.size) / 1000)
            val buffer = ShortArray(totalSamples)

            var sampleIndex = 0
            for (freq in notes) {
                val samplesForNote = (sampleRate * noteDurationMs) / 1000
                for (i in 0 until samplesForNote) {
                    val t = i.toDouble() / sampleRate
                    val envelope = exp(-3.0 * i / samplesForNote)
                    val sample = sin(2.0 * PI * freq * t) * envelope
                    if (sampleIndex < buffer.size) {
                        buffer[sampleIndex++] = (sample * Short.MAX_VALUE * 0.75).toInt().toShort()
                    }
                }
            }
            playPcm(buffer)
        }
    }

    override fun playCoinSound() {
        if (!soundEnabled) return
        audioScope.launch {
            val durationMs = 280
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freq1 = 1318.51
            val freq2 = 1975.53

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-6.0 * i / numSamples)
                val sample = (0.6 * sin(2.0 * PI * freq1 * t) + 0.4 * sin(2.0 * PI * freq2 * t)) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    override fun playIncorrectSound() {
        if (!soundEnabled) return
        audioScope.launch {
            val durationMs = 240
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val freq = 260.0 - progress * 80.0
                val envelope = exp(-4.0 * progress)
                val sample = sin(2.0 * PI * freq * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.6).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    override fun playClickSound() {
        if (!soundEnabled) return
        audioScope.launch {
            val durationMs = 35
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-12.0 * i / numSamples)
                val sample = sin(2.0 * PI * 880.0 * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.4).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    override fun playCountdownTick() {
        if (!soundEnabled) return
        audioScope.launch {
            val durationMs = 25
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-18.0 * i / numSamples)
                val sample = sin(2.0 * PI * 1200.0 * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.3).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val bufferSize = buffer.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep((buffer.size.toDouble() / sampleRate * 1000).toLong() + 50)
            track.stop()
            track.release()
        } catch (_: Exception) {
            // Audio error handling
        }
    }
}
