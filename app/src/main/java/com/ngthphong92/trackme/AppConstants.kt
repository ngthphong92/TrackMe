package com.ngthphong92.trackme

// Databases
const val DEFAULT_DB = "ngthphong92_track_me.db"
const val HISTORY_SESSION_FILENAME = "session_history.json"

// Session tracking state
const val STATE_STOP = 0
const val STATE_RECORD = 1
const val STATE_PAUSE = 2
const val KEY_SESSION_DATA = "KEY_SESSION_DATA"

// Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
@JvmField val NOTIFICATION_TITLE: CharSequence = "TrackMe Starting"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1
const val TRACK_ME_WORK_NAME = "record_location_work"

// Time format
const val DEFAULT_TIME_FORMAT = "HH:mm:ss"
const val PERIODIC_INTERVAL = 15L

// Map
const val MAP_ZOOM_LEVEL = 15f
