package com.pseteamtwo.allways.ui.map

import android.location.Address

fun addressToString(address: Address): String {
    return (address.thoroughfare ?: "") + " " + (address.subThoroughfare ?: "") + ", " + /*(address.postalCode ?: "" ) + " " +*/ (address.locality ?: "")
}