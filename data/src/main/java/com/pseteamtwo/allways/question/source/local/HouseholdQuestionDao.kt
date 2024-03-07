package com.pseteamtwo.allways.question.source.local

import androidx.room.Dao

/**
 * This class extends the interface [QuestionDao] to handle questions of
 * the [HouseholdQuestionDatabase].
 */
@Dao
interface HouseholdQuestionDao : QuestionDao {
}