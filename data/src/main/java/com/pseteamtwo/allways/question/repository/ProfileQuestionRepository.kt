package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionnaireNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileQuestionRepository @Inject constructor(
    profileQuestionDao: ProfileQuestionDao,
    profileQuestionNetworkDataSource: ProfileQuestionNetworkDataSource,
    profileQuestionnaireNetworkDataSource: ProfileQuestionnaireNetworkDataSource,
    accountRepository: AccountRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    @ApplicationScope scope: CoroutineScope,
): DefaultQuestionRepository<ProfileQuestionDao,
        ProfileQuestionNetworkDataSource, ProfileQuestionnaireNetworkDataSource>(
    profileQuestionDao, profileQuestionNetworkDataSource, profileQuestionnaireNetworkDataSource,
    accountRepository, dispatcher, scope
) {
}