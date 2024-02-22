package com.pseteamtwo.allways.map

import android.location.Address
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

fun addressToString(address: Address): String {
    return (address.thoroughfare ?: "") + " " + (address.subThoroughfare ?: "") + ", " + /*(address.postalCode ?: "" ) + " " +*/ (address.locality ?: "")
}