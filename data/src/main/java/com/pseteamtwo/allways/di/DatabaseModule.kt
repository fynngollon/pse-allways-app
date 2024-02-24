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
    /*@Provides
    @Singleton
    fun provideProfileQuestionRepository(): ProfileQuestionRepository {
        return ProfileQuestionRepository()
    }

*/

@Singleton
@Provides
fun provideDefaultStatisticsRepository(tripAndStageRepository: TripAndStageRepository): DefaultStatisticsRepository {
    return DefaultStatisticsRepository(tripAndStageRepository)
}


/** Profile Question */
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

    @Singleton
    @Provides
    fun provideProfileQuestionNetworkDataSource() : ProfileQuestionNetworkDataSource
    {
        return ProfileQuestionNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideHouseholdQuestionnaireNetworkDataSource() : HouseholdQuestionnaireNetworkDataSource
    {
        return HouseholdQuestionnaireNetworkDataSource()
    }
/*
    @Singleton
    @Provides
    fun provideDefaultTripNetworkDataSource() : DefaultTripNetworkDataSource {
        return DefaultTripNetworkDataSource()
    }
*/
    /*
    @Singleton
    @Provides
    fun provideDefaultStageNetworkDataSource() : DefaultStageNetworkDataSource {
        return DefaultStageNetworkDataSource()
    }
*/

    @Singleton
    @Provides
    fun provideHouseholdQuestionNetworkDataSource() : HouseholdQuestionNetworkDataSource
    {
        return HouseholdQuestionNetworkDataSource()
    }

    @Singleton
    @Provides
    fun provideProfileQuestionnaireNetworkDataSource(): ProfileQuestionnaireNetworkDataSource {
        return ProfileQuestionnaireNetworkDataSource()
    }
/*
    @Singleton
    @Provides
    fun provideDefaultAccountNetworkDataSource(): DefaultAccountNetworkDataSource {
        return DefaultAccountNetworkDataSource()
    }
*/
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
    fun provideTripDatabase(@ApplicationContext context: Context): TripAndStageDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TripAndStageDatabase::class.java,
            "Trip.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDefaultTripNetworkDataSource() : DefaultTripNetworkDataSource {
        return DefaultTripNetworkDataSource()
    }

    @Provides
    fun provideTripDao(database: TripAndStageDatabase): TripDao = database.tripDao()

    @Provides
    fun provideStageDao(database: TripAndStageDatabase): StageDao = database.stageDao()

    @Provides
    fun provideGpsPointDao(database: TripAndStageDatabase): GpsPointDao = database.gpsPointDao()
}

/** Stage */
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class StageRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindStageRepository(repository: DefaultTripAndStageRepository): TripAndStageRepository
}
*/
@Module
@InstallIn(SingletonComponent::class)
abstract class StageDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindStageDataSource(dataSource: DefaultStageNetworkDataSource): StageNetworkDataSource
}

/*
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
    fun provideStageDao(database: StageDatabase): StageDao = database.stageDao()*/

    @Singleton
    @Provides
    fun provideDefaultStageNetworkDataSource() : DefaultStageNetworkDataSource  {
        return DefaultStageNetworkDataSource()
    }
}

/*
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
*/
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

    @Singleton
    @Provides
    fun provideDefaultAccountNetworkDataSource() : DefaultAccountNetworkDataSource  {
        return DefaultAccountNetworkDataSource()
    }
}