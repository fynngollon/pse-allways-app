package com.pseteamtwo.allways.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pseteamtwo.allways.data.exception.QuestionIdNotFoundException
import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.data.question.repository.HouseholdQuestionRepository
import com.pseteamtwo.allways.data.question.repository.ProfileQuestionRepository
import com.pseteamtwo.allways.data.question.repository.QuestionRepository
import com.pseteamtwo.allways.ui.statistics.StatisticsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Viewmodel to retrieve and update the profile related data for the [StatisticsScreen] and
 * [HomeScreen]
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileQuestionRepository: ProfileQuestionRepository,
    private val householdQuestionRepository: HouseholdQuestionRepository) : ViewModel() {

    private var _profileUiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState(loading = true))
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileQuestionRepository.observeAll().collect {
                questions ->
                var profileQuestionUiStates: MutableList<QuestionUiState> = mutableListOf()

                for (question in questions) {
                    profileQuestionUiStates.add(
                        QuestionUiState(
                            id = question.id,
                            title = question.title,
                            type = question.type,
                            options = question.options,
                            answer = question.answer,
                            sendToServer = false
                        )
                    )
                }

                _profileUiState.update {
                    it.copy(
                        profileQuestions = profileQuestionUiStates,
                        loading = false,
                        serverConnectionFailed = false
                    )
                }
            }

            householdQuestionRepository.observeAll().collect {
                questions ->
                var householdQuestionUiStates: MutableList<QuestionUiState> = mutableListOf()

                for (question in questions) {
                    householdQuestionUiStates.add(
                        QuestionUiState(
                            id = question.id,
                            title = question.title,
                            type = question.type,
                            options = question.options,
                            answer = question.answer,
                            sendToServer = false
                        )
                    )
                }

                _profileUiState.update {
                    it.copy(
                        householdQuestions = householdQuestionUiStates,
                        loading = false,
                        serverConnectionFailed = false
                    )
                }
            }
        }
    }

    /**
     * function to update the answer to a profile related question locally
     * @param id the unique identifier of the question
     * @param answer the new answer to the question
     */
    fun updateProfileAnswer(id: String, answer: String) {
        viewModelScope.launch {
            try {
                profileQuestionRepository.updateAnswer(id, answer)
            } catch (e: QuestionIdNotFoundException) {
            }
        }
    }



    /**
     * function to update the answer to a household related question locally
     * @param id the unique identifier of the question
     * @param answer the new answer to the question
     */
    fun updateHouseholdAnswer(id: String, answer: String) {
        viewModelScope.launch {
            try {
                householdQuestionRepository.updateAnswer(id, answer)
            }catch (e: QuestionIdNotFoundException) {
            }
        }
    }

    /**
     * function to donate a list of profile related questions and send the answers to the server.
     * @param profileQuestions the list of the questions to be send to the server.
     */
    fun donateProfileQuestions(profileQuestions: List<QuestionUiState>) {
        val profileQuestionsToSend: MutableList<String> = mutableListOf()
        for(question in profileQuestions) {
            if(question.sendToServer) {
                profileQuestionsToSend.add(question.id)
            }
        }
        viewModelScope.launch {
            try {
                profileQuestionRepository.saveQuestionsToNetwork(profileQuestionsToSend)
            }catch (e: ServerConnectionFailedException) {

            }
        }
    }

    /**
     * function to donate a list of household related questions and send the answers to the server.
     * @param householdQuestions the list of the questions to be send to the server.
     */
    fun donateHouseholdQuestions(householdQuestions: List<QuestionUiState>) {
        val householdQuestionsToSend: MutableList<String> = mutableListOf()
        for(question in householdQuestions) {
            if(question.sendToServer) {
                householdQuestionsToSend.add(question.id)
            }
        }
        viewModelScope.launch {
            try {
                householdQuestionRepository.saveQuestionsToNetwork(householdQuestionsToSend)
            } catch (e: ServerConnectionFailedException) {

            }
        }
    }

    /**
     * function to set the boolean value ServerConnectionFailed.
     * @param value the new value of the Boolean ServerConnectionFailed.
     */
    fun setServerConnectionFailed(value: Boolean) {
        _profileUiState.value.copy(serverConnectionFailed = value)
    }
}

