package com.needtools.mymeds.util

import android.content.Context
import android.media.MediaPlayer
import com.needtools.mymeds.R

object PlaySound {
    fun playClickSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.button_on)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }
    fun playNotificationSound(context: Context) {
        if (!SettingsManager(context).isSoundEnabled()) return

        val mediaPlayer = MediaPlayer.create(context, R.raw.notification)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }
}