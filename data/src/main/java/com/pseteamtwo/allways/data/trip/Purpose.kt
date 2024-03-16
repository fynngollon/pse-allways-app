package com.pseteamtwo.allways.data.trip

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pseteamtwo.allways.R
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
enum class Purpose(private val purposeType: Int, var purposeDetail: String? = null) {

    /**
     * Represents that no Purpose has been set for [Trip] yet.
     */
    NONE(R.string.purpose_none),

    /**
     * [Trip] got traveled to go to work.
     */
    WORK(R.string.purpose_work),

    /**
     * [Trip] is a business trip.
     */
    BUSINESS_TRIP(R.string.purpose_business_trip),

    /**
     * [Trip] got traveled to go to an educational institution like school, university
     * or to an skill enhancement.
     */
    EDUCATION(R.string.purpose_education),

    /**
     * [Trip] got traveled to go shopping.
     */
    SHOPPING(R.string.purpose_shopping),

    /**
     * [Trip] got traveled for leisure or vacation reasons.
     */
    LEISURE(R.string.purpose_leisure),

    /**
     * [Trip] got traveled for transportation reasons.
     */
    TRANSPORTATION(R.string.purpose_transportation),

    /**
     * [Trip] got traveled to do other errands.
     */
    OTHER_ERRANDS(R.string.purpose_other_errands),

    /**
     * [Trip] got traveled to go home.
     */
    HOME(R.string.purpose_home),

    /**
     * [Trip] got traveled to do something this enumeration does not contain.
     */
    OTHER(R.string.purpose_other);


    @Composable
    fun getStringForPurpose(): String {
        return stringResource(id = this.purposeType)
    }
}