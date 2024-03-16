package com.pseteamtwo.allways.data.di

import android.content.Context
import androidx.room.Room
import com.pseteamtwo.allways.data.account.repository.AccountRepository
import com.pseteamtwo.allways.data.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.data.account.source.local.AccountDao
import com.pseteamtwo.allways.data.account.source.local.AccountDatabase
import com.pseteamtwo.allways.data.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.data.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.data.question.repository.HouseholdQuestionRepository
import com.pseteamtwo.allways.data.question.repository.ProfileQuestionRepository
import com.pseteamtwo.allways.data.question.repository.QuestionRepository
import com.pseteamtwo.allways.data.question.source.local.HouseholdQuestionDao
import com.pseteamtwo.allways.data.question.source.local.HouseholdQuestionDatabase
import com.pseteamtwo.allways.data.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.data.question.source.local.ProfileQuestionDatabase
import com.pseteamtwo.allways.data.question.source.network.HouseholdQuestionNetworkDataSource
import com.pseteamtwo.allways.data.question.source.network.HouseholdQuestionnaireNetworkDataSource
import com.pseteamtwo.allways.data.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.data.question.source.network.ProfileQuestionnaireNetworkDataSource
import com.pseteamtwo.allways.data.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.data.statistics.DefaultStatisticsRepository
import com.pseteamtwo.allways.data.statistics.StatisticsRepository
import com.pseteamtwo.allways.data.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.data.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.data.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.data.trip.source.local.StageDao
import com.pseteamtwo.allways.data.trip.source.local.TripAndStageDatabase
import com.pseteamtwo.allways.data.trip.source.local.TripDao
import com.pseteamtwo.allways.data.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.TripNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDefaultAccountNetworkDataSource(): DefaultAccountNetworkDataSource {
        return DefaultAccountNetworkDataSource()

    }

    @Singleton
    @Provides
    fun provideDefaultTripNetworkDataSource(): DefaultTripNetworkDataSource {
        return DefaultTripNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideDefaultStageNetworkDataSource(): DefaultStageNetworkDataSource {
        return DefaultStageNetworkDataSource()
    }

   /* @Singleton
    @Provides
    fun provideQuestionRepository(profileQuestionDao: ProfileQuestionDao,
                                  profileQuestionNetworkDataSource: ProfileQuestionNetworkDataSource,
                                  profileQuestionnaireNetworkDataSource: ProfileQuestionnaireNetworkDataSource,
                                  accountRepository: AccountRepository,
                                  @DefaultDispatcher dispatcher: CoroutineDispatcher,) : QuestionRepository {
        return ProfileQuestionRepository(
            profileQuestionDao,
            profileQuestionNetworkDataSource,
            profileQuestionnaireNetworkDataSource,
            accountRepository,
            dispatcher
        )
    }*/

    @Singleton
    @Provides
    fun provideProfileQuestionNetworkDataSource(): ProfileQuestionNetworkDataSource {
        return ProfileQuestionNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideHouseholdQuestionNetworkDataSource(): HouseholdQuestionNetworkDataSource {
        return HouseholdQuestionNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideHouseholdQuestionnaireNetworkDataSource(): HouseholdQuestionnaireNetworkDataSource {
        return HouseholdQuestionnaireNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideProfileQuestionnaireNetworkDataSource(): ProfileQuestionnaireNetworkDataSource {
        return ProfileQuestionnaireNetworkDataSource()
    }

    @Provides
    fun provideAccountDao(database: AccountDatabase): AccountDao = database.accountDao()


    @Provides
    fun provideProfileQuestionDao(
        database: ProfileQuestionDatabase
    ): ProfileQuestionDao = database.profileQuestionDao()

    @Provides
    fun provideHouseholdQuestionDao(database: HouseholdQuestionDatabase):
            HouseholdQuestionDao = database.householdQuestionDao()

    @Provides
    fun provideTripDao(database: TripAndStageDatabase): TripDao = database.tripDao()

    @Provides
    fun provideStageDao(database: TripAndStageDatabase): StageDao = database.stageDao()


    @Provides
    fun provideGpsPointDao(database: TripAndStageDatabase): GpsPointDao = database.gpsPointDao()

    @Provides
    @Singleton
    fun provideAppPreferences(context: Application): AppPreferences = AppPreferences(context.applicationContext)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAccountRepository(repository: DefaultAccountRepository): AccountRepository

    @Singleton
    @Binds
    abstract fun bindStatisticsRepository(repository: DefaultStatisticsRepository):
            StatisticsRepository

    @Singleton
    @Binds
    abstract fun bindProfileQuestionRepository(
        repository: ProfileQuestionRepository): QuestionRepository

    @Singleton
    @Binds
    abstract fun bindHouseholdQuestionRepository(
        repository: HouseholdQuestionRepository): QuestionRepository


    @Singleton
    @Binds
    abstract fun bindTripRepository(repository: DefaultTripAndStageRepository):
            TripAndStageRepository

}

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    fun provideAccountDatabase(@ApplicationContext context: Context): AccountDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AccountDatabase::class.java,
            "Account.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideProfileQuestionDatabase(
        @ApplicationContext context: Context
    ): ProfileQuestionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProfileQuestionDatabase::class.java,
            "ProfileQuestion.db"
        ).build()
    }


    @Singleton
    @Provides
    fun provideHouseholdQuestionDatabase(
        @ApplicationContext context: Context
    ): HouseholdQuestionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HouseholdQuestionDatabase::class.java,
            "HouseholdQuestion.db"
        ).build()
    }


    @Singleton
    @Provides
    fun provideTripDatabase(@ApplicationContext context: Context):
            TripAndStageDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TripAndStageDatabase::class.java,
            "Trip.db"
        ).build()
    }
}


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceBindModule {

    @Singleton
    @Binds
    abstract fun bindStageDataSource(dataSource: DefaultStageNetworkDataSource):
            StageNetworkDataSource

    @Singleton
    @Binds
    abstract fun bindTripDataSource(dataSource: DefaultTripNetworkDataSource):
            TripNetworkDataSource

    @Singleton
    @Binds
    abstract fun bindHouseholdQuestionDataSource(
        dataSource: HouseholdQuestionNetworkDataSource
    ): QuestionNetworkDataSource

    @Singleton
    @Binds
    abstract fun bindProfileQuestionDataSource(
        dataSource: ProfileQuestionNetworkDataSource
    ): QuestionNetworkDataSource

    @Singleton
    @Binds
    abstract fun bindAccountDataSource(dataSource: DefaultAccountNetworkDataSource):
            AccountNetworkDataSource
}
