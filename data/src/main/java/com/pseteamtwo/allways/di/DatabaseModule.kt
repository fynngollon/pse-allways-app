package com.pseteamtwo.allways.di

import android.content.Context
import androidx.room.Room
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.question.repository.HouseholdQuestionRepository
import com.pseteamtwo.allways.question.repository.ProfileQuestionRepository
import com.pseteamtwo.allways.question.repository.QuestionRepository
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDao
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDatabase
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDatabase
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.statistics.DefaultStatisticsRepository
import com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripAndStageDatabase
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

    @Singleton
    @Provides
    fun provideDefaultStatisticsRepository(
        tripAndStageRepository: TripAndStageRepository
    ): DefaultStatisticsRepository {
        return DefaultStatisticsRepository(tripAndStageRepository)
    }

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

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAccountRepository(repository: DefaultAccountRepository): AccountRepository


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
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindAccountDataSource(dataSource: DefaultAccountNetworkDataSource):
            AccountNetworkDataSource

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
    @Binds
    abstract fun bindProfileQuestionDataSource(
        dataSource: ProfileQuestionNetworkDataSource
    ): QuestionNetworkDataSource

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
    @Binds
    abstract fun bindHouseholdQuestionDataSource(
        dataSource: HouseholdQuestionNetworkDataSource
    ): QuestionNetworkDataSource

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
    @Binds
    abstract fun bindTripDataSource(dataSource: DefaultTripNetworkDataSource):
            TripNetworkDataSource

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

    @Singleton
    @Binds
    abstract fun bindStageDataSource(dataSource: DefaultStageNetworkDataSource):
            StageNetworkDataSource

}