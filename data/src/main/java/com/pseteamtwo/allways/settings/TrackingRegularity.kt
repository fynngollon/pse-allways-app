package com.pseteamtwo.allways.settings

import kotlinx.serialization.Serializable

@Serializable
enum class TrackingRegularity(val regularity: Int) {
    NEVER(0),
    RARELY(60),
    MEDIUM(30),
    OFTEN(10);
}