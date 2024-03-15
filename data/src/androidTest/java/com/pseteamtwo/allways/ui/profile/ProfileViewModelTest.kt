package com.pseteamtwo.allways.ui.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.profile.ProfileViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

    private lateinit var fakeHouseHoldQuestionRepository: com.pseteamtwo.allways.ui.profile.FakeQuestionRepository
    private lateinit var fakeProfileQuestionRepository: com.pseteamtwo.allways.ui.profile.FakeQuestionRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        fakeProfileQuestionRepository = com.pseteamtwo.allways.ui.profile.FakeQuestionRepository()
        fakeHouseHoldQuestionRepository = com.pseteamtwo.allways.ui.profile.FakeQuestionRepository()
        viewModel = ProfileViewModel(fakeProfileQuestionRepository, fakeHouseHoldQuestionRepository)
    }

    @Test
    fun testUpdateProfileAnswer() = runTest {
        viewModel.updateProfileAnswer("1", "updated answer")
        assertEquals(viewModel.profileUiState.value.profileQuestions[0].answer, "updated answer")
    }
}