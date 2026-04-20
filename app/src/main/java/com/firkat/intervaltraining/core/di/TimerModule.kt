package com.firkat.intervaltraining.core.di

import com.firkat.intervaltraining.feature.training.sound.SystemTimerSoundPlayer
import com.firkat.intervaltraining.feature.training.sound.TimerSoundPlayer
import com.firkat.intervaltraining.feature.training.timer.SystemTimerClock
import com.firkat.intervaltraining.feature.training.timer.TimerClock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerModule {
    @Binds
    @Singleton
    abstract fun bindTimerClock(impl: SystemTimerClock): TimerClock

    @Binds
    @Singleton
    abstract fun bindTimerSoundPlayer(impl: SystemTimerSoundPlayer): TimerSoundPlayer
}
