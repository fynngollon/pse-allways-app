package com.pseteamtwo.allways.data.trip

import com.pseteamtwo.allways.data.trip.Purpose.OTHER
import kotlinx.serialization.Serializable

/**
 * Purpose
 *
 * This enum class describes different purposes a [Trip] got traveled for.
 * Every [Trip] contains exactly 1 [Purpose] object.
 *
 * @property purposeDetail Describes a [Purpose] object in detail.
 * Especially used for [OTHER] to clarify which other purpose is meant.
 * Can be null and is initially set to null until detailed description is provided.
 */
@Serializable
enum class Purpose(val purposeType: String, var purposeDetail: String? = null) {
    //TODO("implementation of use of purposeDetail")

    /**
     * Represents that no Purpose has been set for [Trip] yet.
     */
    NONE("(Nicht eingetragen)"),

    /**
     * [Trip] got traveled to go to work.
     */
    WORK("Arbeit"),

    /**
     * [Trip] is a business trip.
     */
    BUSINESS_TRIP("Geschäftsreise"),

    /**
     * [Trip] got traveled to go to an educational institution like school, university
     * or to an skill enhancement.
     */
    EDUCATION("Schule/Studium/Fortbildung"),

    /**
     * [Trip] got traveled to go shopping.
     */
    SHOPPING("Einkaufen"),

    /**
     * [Trip] got traveled for leisure or vacation reasons.
     */
    LEISURE("Freizeit"),

    /**
     * [Trip] got traveled for transportation reasons.
     */
    TRANSPORTATION("Etw./Jmd. transportieren"),

    /**
     * [Trip] got traveled to do other errands.
     */
    OTHER_ERRANDS("Andere Erledigungen"),

    /**
     * [Trip] got traveled to go home.
     */
    HOME("Nach Hause gehen"),

    /**
     * [Trip] got traveled to do something this enumeration does not contain.
     */
    OTHER("Andere");
}