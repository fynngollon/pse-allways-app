package com.pseteamtwo.allways.di

import android.content.Context
import androidx.room.Room
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.question.repository.HouseholdQuestionRepository
import com.pseteamtwo.allways.question.repository.ProfileQuestionRepository
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDao
import com.pseteamtwo.allways.question.source.local.HouseholdQuestionDatabase
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDatabase
import com.pseteamtwo.allways.question.source.local.QuestionDao
import com.pseteamtwo.allways.question.source.network.HouseholdQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.GpsPointDatabase
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.StageDatabase
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.local.TripDatabase
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import com.pseteamtwo.allways.typeconverter.ListOfLocalGpsPointConverter
import com.pseteamtwo.allways.typeconverter.ListOfLocalStageConverter
import com.pseteamtwo.allways.typeconverter.LocationConverter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Profile Question */
@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileQuestionRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindProfileQuestionRepository(repository: ProfileQuestionRepository): ProfileQuestionRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileQuestionDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindProfileQuestionDataSource(dataSource: ProfileQuestionNetworkDataSource): ProfileQuestionNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object ProfileQuestionDatabaseModule {

    @Singleton
    @Provides
    fun provideProfileQuestionDatabase(@ApplicationContext context: Context): ProfileQuestionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProfileQuestionDatabase::class.java,
            "ProfileQuestion.db"
        ).build()
    }

    @Provides
    fun provideProfileQuestionDao(database: ProfileQuestionDatabase): ProfileQuestionDao = database.profileQuestionDao()
}


/** Profile Question */
@Module
@InstallIn(SingletonComponent::class)
abstract class HouseholdQuestionRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindHouseholdQuestionRepository(repository: HouseholdQuestionRepository): HouseholdQuestionRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HouseholdQuestionDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindHouseholdQuestionDataSource(dataSource: HouseholdQuestionNetworkDataSource): HouseholdQuestionNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object HouseholdQuestionDatabaseModule {

    @Singleton
    @Provides
    fun provideHouseholdQuestionDatabase(@ApplicationContext context: Context): HouseholdQuestionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HouseholdQuestionDatabase::class.java,
            "HouseholdQuestion.db"
        ).build()
    }

    @Provides
    fun provideHouseholdQuestionDao(database: HouseholdQuestionDatabase): HouseholdQuestionDao = database.householdQuestionDao()
}

/** Trip */
@Module
@InstallIn(SingletonComponent::class)
abstract class TripRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTripRepository(repository: TripAndStageRepository): TripAndStageRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TripDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindTripDataSource(dataSource: TripNetworkDataSource): TripNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object TripDatabaseModule {

    @Singleton
    @Provides
    fun provideTripDatabase(@ApplicationContext context: Context): TripDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TripDatabase::class.java,
            "Trip.db"
        ).build()
    }

    @Provides
    fun provideTripDao(database: TripDatabase): TripDao = database.tripDao()
}

/** Stage */
@Module
@InstallIn(SingletonComponent::class)
abstract class StageRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindStageRepository(repository: TripAndStageRepository): TripAndStageRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StageDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindStageDataSource(dataSource: StageNetworkDataSource): StageNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object StageDatabaseModule {

    @Singleton
    @Provides
    fun provideStageDatabase(@ApplicationContext context: Context): StageDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StageDatabase::class.java,
            "Stage.db"
        ).build()
    }

    @Provides
    fun provideStageDao(database: StageDatabase): StageDao = database.stageDao()
}

/** GPS Point */
@Module
@InstallIn(SingletonComponent::class)
abstract class GpsPointRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindGpsPointRepository(repository: TripAndStageRepository): TripAndStageRepository
}

@Module
@InstallIn(SingletonComponent::class)
object GpsPointDatabaseModule {

    @Singleton
    @Provides
    fun provideGpsPointDatabase(@ApplicationContext context: Context): GpsPointDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GpsPointDatabase::class.java,
            "GpsPoint.db"
        ).build()
    }

    @Provides
    fun provideGpsPointDao(database: GpsPointDatabase): GpsPointDao = database.gpsPointDao()
}

/** Account */
@Module
@InstallIn(SingletonComponent::class)
abstract class AccountRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAccountRepository(repository: AccountRepository): AccountRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindAccountDataSource(dataSource: AccountNetworkDataSource): AccountNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object AccountDatabaseModule {

    @Singleton
    @Provides
    fun provideAccountDatabase(@ApplicationContext context: Context): AccountDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AccountDatabase::class.java,
            "Account.db"
        ).build()
    }

    @Provides
    fun provideAccountDao(database: AccountDatabase): AccountDao = database.accountDao()
}
