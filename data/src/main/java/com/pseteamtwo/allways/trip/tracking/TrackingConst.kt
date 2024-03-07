package com.pseteamtwo.allways.trip.tracking

// Shared constants
const val ACTION_START = "ACTION_START"
const val ACTION_STOP = "ACTION_STOP"
const val GPS_DISABLED = "GPS is disabled"

// Location Tracking constants
const val LOCATION_TRACKING_PERMISSION_MISSING = "Missing location tracking permission!"
const val LOCATION_TRACKING_CHANNEL_ID = "CHANNEL_ID_LOCATION_TRACKING"
const val LOCATION_TRACKING_NOTIFICATION_ID = 1
const val LOCATION_TRACKING_NOTIFICATION_TITLE = "Tracking location"
const val LOCATION_TRACKING_NOTIFICATION_NAME = "Location Tracking"
const val LOCATION_TRACKING_NOTIFICATION_TEXT= "Last tracked location (%f, %f)"

// Activity Transition Recognition constants
const val ACTIVITY_RECOGNITION_PERMISSION_MISSING = "Missing activity recognition permission!"
const val ACTIVITY_RECOGNITION_CHANNEL_ID = "CHANNEL_ID_ACTIVITY_RECOGNITION"
const val ACTIVITY_RECOGNITION_NOTIFICATION_ID = 2
const val ACTIVITY_RECOGNITION_NOTIFICATION_TITLE = "AllWays - Standort-Tracking"
const val ACTIVITY_RECOGNITION_NOTIFICATION_NAME = "Activity Transition"
const val ACTIVITY_RECOGNITION_NOTIFICATION_TEXT = "Die App benutzt deinen Standort."