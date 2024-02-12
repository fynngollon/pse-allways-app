package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDao
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.QuestionnaireNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdQuestionRepository @Inject constructor(
    householdQuestionDao: HouseholdQuestionDao,
    householdQuestionNetworkDataSource: HouseholdQuestionNetworkDataSource,
    householdQuestionnaireNetworkDataSource: QuestionnaireNetworkDataSource,
    accountRepository: AccountRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationScope scope: CoroutineScope,
): DefaultQuestionRepository<HouseholdQuestionDao, HouseholdQuestionNetworkDataSource>(
    householdQuestionDao, householdQuestionNetworkDataSource, householdQuestionnaireNetworkDataSource,
    accountRepository, dispatcher, scope
) {
}