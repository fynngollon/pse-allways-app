package com.pseteamtwo.allways.settings

enum class TrackingRegularity(val regularity: Long) {
    NEVER(0),
    RARELY(60),
    MEDIUM(30),
    OFTEN(10);
}