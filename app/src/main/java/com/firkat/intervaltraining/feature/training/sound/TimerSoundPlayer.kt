package com.firkat.intervaltraining.feature.training.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface TimerSoundPlayer {
    fun playSignal()
}

@Singleton
class SystemTimerSoundPlayer
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : TimerSoundPlayer {
        private val audioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        override fun playSignal() {
            val player =
                runCatching {
                    MediaPlayer.create(
                        context,
                        Settings.System.DEFAULT_NOTIFICATION_URI,
                        null,
                        audioAttributes,
                        AudioManager.AUDIO_SESSION_ID_GENERATE,
                    )
                }.getOrNull() ?: return

            runCatching {
                player.setVolume(MAX_VOLUME, MAX_VOLUME)
                player.setOnCompletionListener { completedPlayer ->
                    completedPlayer.release()
                }
                player.setOnErrorListener { failedPlayer, _, _ ->
                    failedPlayer.release()
                    true
                }
                player.start()
            }.onFailure {
                player.release()
            }
        }

        private companion object {
            const val MAX_VOLUME = 1f
        }
    }
