package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionnaireNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class extends [DefaultQuestionRepository] to handle all interactions with
 * questions referring to the profile information of the user.
 * This class follows the singleton-pattern.
 *
 * @param profileQuestionDao To access the local profile question database.
 * @param profileQuestionNetworkDataSource To access the network profile question database.
 * @param profileQuestionnaireNetworkDataSource To access the network database providing all
 * questions referring to the profile information of the user.
 * @param accountRepository To access the user's account data for saving
 * and retrieving data from the network database.
 * @param dispatcher A dispatcher to allow asynchronous function calls because this class uses
 * complex computing and many accesses to databases which shall not block the program flow.
 * @constructor Creates an instance of this class.
 */
@Singleton
class ProfileQuestionRepository @Inject constructor(
    profileQuestionDao: ProfileQuestionDao,
    profileQuestionNetworkDataSource: ProfileQuestionNetworkDataSource,
    profileQuestionnaireNetworkDataSource: ProfileQuestionnaireNetworkDataSource,
    accountRepository: AccountRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    //@ApplicationScope scope: CoroutineScope,
): DefaultQuestionRepository<ProfileQuestionDao,
        ProfileQuestionNetworkDataSource, ProfileQuestionnaireNetworkDataSource>(
    profileQuestionDao, profileQuestionNetworkDataSource, profileQuestionnaireNetworkDataSource,
    accountRepository, dispatcher, //scope
) {
}