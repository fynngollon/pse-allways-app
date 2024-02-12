package com.fynng.allways.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pseteamtwo.allways.question.repository.ProfileQuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileQuestionRepository: ProfileQuestionRepository) : ViewModel() {

    private var _profileUiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState(loading = true))
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileQuestionRepository.observeAll().collect {
                questions ->
                var questionUiStates: MutableList<QuestionUiState> = mutableListOf()
                for (question in questions) {
                    questionUiStates.add(
                        QuestionUiState(
                            id = question.id,
                            title = question.title,
                            type = question.type,
                            options = question.options,
                            answer = question.answer
                        )
                    )
                }

                _profileUiState.update {
                    it ->
                    it.copy(
                        questions = questionUiStates,
                        loading = false,
                        serverConnectionFailed = false
                    )
                }
            }
        }
    }
}

