package com.pseteamtwo.allways.trip

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
enum class Purpose(var purposeDetail: String? = null) {
    /**
     * Represents that no Purpose has been set for [Trip] yet.
     */
    NONE,

    /**
     * [Trip] got traveled to go to work.
     */
    WORK,

    /**
     * [Trip] is a business trip.
     */
    BUSINESS_TRIP,

    /**
     * [Trip] got traveled to go to an educational institution like school, university
     * or to an skill enhancement.
     */
    EDUCATION,

    /**
     * [Trip] got traveled to go shopping.
     */
    SHOPPING,

    /**
     * [Trip] got traveled for leisure or vacation reasons.
     */
    LEISURE,

    /**
     * [Trip] got traveled for transportation reasons.
     */
    TRANSPORTATION,

    /**
     * [Trip] got traveled to do other errands.
     */
    OTHER_ERRANDS,

    /**
     * [Trip] got traveled to go home.
     */
    HOME,

    /**
     * [Trip] got traveled to do something this enumeration does not contain.
     */
    OTHER;
}