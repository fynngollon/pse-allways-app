package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDao
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionnaireNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class extends [DefaultQuestionRepository] to handle all interactions with
 * questions referring to the household information of the user.
 * This class follows the singleton-pattern.
 *
 * @param householdQuestionDao To access the local household question database.
 * @param householdQuestionNetworkDataSource To access the network household question database.
 * @param householdQuestionnaireNetworkDataSource To access the network database providing all
 * questions referring to the household information of the user.
 * @param accountRepository To access the user's account data for saving
 * and retrieving data from the network database.
 * @param dispatcher A dispatcher to allow asynchronous function calls because this class uses
 * complex computing and many accesses to databases which shall not block the program flow.
 * @constructor Creates an instance of this class.
 */
@Singleton
class HouseholdQuestionRepository @Inject constructor(
    householdQuestionDao: HouseholdQuestionDao,
    householdQuestionNetworkDataSource: HouseholdQuestionNetworkDataSource,
    householdQuestionnaireNetworkDataSource: HouseholdQuestionnaireNetworkDataSource,
    accountRepository: AccountRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    //@ApplicationScope scope: CoroutineScope,
): DefaultQuestionRepository<HouseholdQuestionDao,
        HouseholdQuestionNetworkDataSource, HouseholdQuestionnaireNetworkDataSource>(
    householdQuestionDao, householdQuestionNetworkDataSource,
    householdQuestionnaireNetworkDataSource,
    accountRepository, dispatcher, //scope
) {
}