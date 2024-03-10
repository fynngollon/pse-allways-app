package com.pseteamtwo.allways.trip.repository

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DefaultTripAndStageRepositoryTest {

    private lateinit var repository: DefaultTripAndStageRepository

    @Mock private lateinit var tripLocalDataSource: TripDao
    @Mock private lateinit var tripNetworkDataSource: TripNetworkDataSource
    @Mock private lateinit var stageLocalDataSource: StageDao
    @Mock private lateinit var stageNetworkDataSource: StageNetworkDataSource
    @Mock private lateinit var gpsPointLocalDataSource: GpsPointDao
    @Mock private lateinit var accountRepository: AccountRepository
    @Mock private lateinit var testDispatcher: TestDispatcher
    @Mock private lateinit var scope: TestScope


    @Before
    fun setUp() {
        // Initialize mocks and repository instance
        MockitoAnnotations.openMocks(this)
        repository = DefaultTripAndStageRepository(
            tripLocalDataSource,
            tripNetworkDataSource,
            stageLocalDataSource,
            stageNetworkDataSource,
            gpsPointLocalDataSource,
            accountRepository,
            testDispatcher,
            scope
        )
    }

    @Test
    fun `create gps point`() {
        // Create sample trip and stage data
        val location = Location("Test")
        location.latitude = 0.0
        location.longitude = 0.0
        location.time = System.currentTimeMillis()
        location.speed = 0F

        runBlocking {
            //val localGpsPoint = repository.createGpsPoint(location).toLocal(null)

            // Verify that the data was saved to the local data sources
            //verify(gpsPointLocalDataSource).insert(localGpsPoint)
        }
    }
}