package com.pseteamtwo.allways.ui.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.data.question.repository.HouseholdQuestionRepository
import com.pseteamtwo.allways.data.question.repository.ProfileQuestionRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

   private lateinit var profileQuestionRepository: ProfileQuestionRepository
   private lateinit var householdQuestionRepository: HouseholdQuestionRepository
   private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {

    }

    @Test
    fun testUpdateProfileAnswer() = runTest {
        viewModel.updateProfileAnswer("1", "updated answer")
        assertEquals(viewModel.profileUiState.value.profileQuestions[0].answer, "updated answer")
    }
}

