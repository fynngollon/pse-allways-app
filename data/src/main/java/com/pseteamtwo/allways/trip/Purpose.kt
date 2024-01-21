package com.pseteamtwo.allways.trip

enum class Purpose(var purposeString: String? = null) {
    NONE,
    WORK,
    BUSINESS_TRIP,
    EDUCATION,
    SHOPPING,
    LEISURE,
    TRANSPORTATION,
    OTHER_ERRANDS,
    HOME,
    OTHER;
}