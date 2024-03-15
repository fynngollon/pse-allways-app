package com.pseteamtwo.allways.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

    private lateinit var fakeHouseHoldQuestionRepository: FakeQuestionRepository
    private lateinit var fakeProfileQuestionRepository: FakeQuestionRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        fakeProfileQuestionRepository = FakeQuestionRepository()
        fakeHouseHoldQuestionRepository = FakeQuestionRepository()
        viewModel = ProfileViewModel(fakeProfileQuestionRepository, fakeHouseHoldQuestionRepository)
    }

    @Test
    fun testUpdateProfileAnswer() = runTest {
        viewModel.updateProfileAnswer("1", "updated answer")
        assertEquals(viewModel.profileUiState.value.profileQuestions[0].answer, "updated answer")
    }
}