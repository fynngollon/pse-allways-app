package com.pseteamtwo.allways.data.trip.tracking.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pseteamtwo.allways.data.settings.AppPreferences
import com.pseteamtwo.allways.data.trip.tracking.ACTION_START

class PhoneRestartBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("PSE_TRACKING", "PhoneRestartBroadcastReceiver: phone started")
            if (AppPreferences(context).isTrackingEnabled) {
                Intent(context, LocationService::class.java).apply {
                    action = ACTION_START
                    context.startService(this)
                }
            }
        }
    }
}