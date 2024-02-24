package com.pseteamtwo.allways.trip.tracking

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class TrackingService : Service() {

    internal val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    internal abstract fun start()

    internal fun stop() {
        stopForeground(true)
        stopSelf()
    }

    internal abstract fun buildNotification(): NotificationCompat.Builder

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}