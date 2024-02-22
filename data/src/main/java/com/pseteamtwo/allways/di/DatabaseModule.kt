package com.pseteamtwo.allways.di

import com.pseteamtwo.allways.question.repository.ProfileQuestionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideProfileQuestionRepository(): ProfileQuestionRepository {
        return ProfileQuestionRepository()
    }
}





/** Profile Question */
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileQuestionRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindProfileQuestionRepository(repository: ProfileQuestionRepository): QuestionRepository // TODO potential the interface as return value
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileQuestionDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindProfileQuestionDataSource(dataSource: ProfileQuestionNetworkDataSource): QuestionNetworkDataSource // TODO potential the interface as return value
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
    abstract fun bindHouseholdQuestionRepository(repository: HouseholdQuestionRepository): QuestionRepository // TODO potential not the interface as return value
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HouseholdQuestionDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindHouseholdQuestionDataSource(dataSource: HouseholdQuestionNetworkDataSource): QuestionNetworkDataSource // TODO potential not the interface as return value
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
    abstract fun bindTripRepository(repository: DefaultTripAndStageRepository): TripAndStageRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TripDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindTripDataSource(dataSource: DefaultTripNetworkDataSource): TripNetworkDataSource
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
    abstract fun bindStageRepository(repository: DefaultTripAndStageRepository): TripAndStageRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StageDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindStageDataSource(dataSource: DefaultStageNetworkDataSource): StageNetworkDataSource
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
    abstract fun bindGpsPointRepository(repository: DefaultTripAndStageRepository): TripAndStageRepository
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
    abstract fun bindAccountRepository(repository: DefaultAccountRepository): AccountRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindAccountDataSource(dataSource: DefaultAccountNetworkDataSource): AccountNetworkDataSource
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
}*/