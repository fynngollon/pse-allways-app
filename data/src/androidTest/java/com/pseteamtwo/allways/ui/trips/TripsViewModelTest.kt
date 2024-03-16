package com.pseteamtwo.allways.ui.trips

import com.pseteamtwo.allways.data.trip.Mode
import com.pseteamtwo.allways.data.trip.Purpose
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class TripsViewModelTest {

    private lateinit var fakeRepository: FakeTripAndStageRepository
    private lateinit var viewModel: TripsViewModel

    private lateinit var tripUiState: TripUiState

    @Before
    fun setUp() {
        fakeRepository = FakeTripAndStageRepository()
        viewModel = TripsViewModel(fakeRepository)
    }

    @Test
    fun testAddTrip() = runTest{
        viewModel.addTrip()
        assertNotNull(viewModel.tripsUiState.value.tripUiStates.first())
    }

    @Test
    fun testDeleteTripUiState() = runTest {
        viewModel.addTrip()
        viewModel.deleteTripUiState(tripUiStateId = 0)
        assert(viewModel.tripsUiState.value.tripUiStates.isEmpty())
    }

    @Test
    fun testSetTripUiStatePurpose() = runTest {
        viewModel.addTrip()
        viewModel.setTripUiStatePurpose(0, Purpose.BUSINESS_TRIP)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().purpose, Purpose.BUSINESS_TRIP)
    }

    @Test
    fun testSetStageUiStateMode() = runTest {
        viewModel.addTrip()
        viewModel.setStageUiStageMode(tripUiStateId = 0, stageUiStateId = 0, mode = Mode.E_BIKE)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().mode, Mode.E_BIKE)
    }

    @Test
    fun testSetStageUiStageStartDate() = runTest {
        viewModel.addTrip()
        viewModel.setStageUiStageStartDate(0, 0, LocalDate.now())
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().startDate, LocalDate.now())
    }

    @Test
    fun testSetStageUiStageEndDate() = runTest {
        viewModel.addTrip()
        viewModel.setStageUiStageEndDate(0, 0, LocalDate.now())
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().startDate, LocalDate.now())
    }

    @Test
    fun testSetStageUiStateStartTime() = runTest {
        var hour = LocalDateTime.now().hour
        var min = LocalDateTime.now().minute

        viewModel.addTrip()
        var date = viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().startDate
        viewModel.setStageUiStateStartTime(0, 0, hour, min)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first()
            .stageUiStates.first().startDateTime, LocalDateTime.of(date, LocalTime.of(hour, min)))

    }

    @Test
    fun testSetStageUiStateEndTime() = runTest {
        var hour = LocalDateTime.now().hour
        var min = LocalDateTime.now().minute

        viewModel.addTrip()
        var date = viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().endDate
        viewModel.setStageUiStateEndTime(0, 0, hour, min)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first()
            .stageUiStates.first().endDateTime, LocalDateTime.of(date, LocalTime.of(hour, min)))
    }

    @Test
    fun testSetStageUiStateStartLocation() = runTest {
        var point = GeoPoint(49.009592,8.41512)
        viewModel.addTrip()
        viewModel.setStageUiStateStartLocation(0, 0, point)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().startLocation, point)
    }

    @Test
    fun testSetStageUiStateEndLocation() = runTest {
        var point = GeoPoint(49.009592,8.41512)
        viewModel.addTrip()
        viewModel.setStageUiStateEndLocation(0, 0, point)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().endLocation, point)
    }

    @Test
    fun testSetStageUiStateStartLocationName() = runTest {
        var name = "new location name"
        viewModel.addTrip()
        viewModel.setStageUiStateStartLocationName(0, 0, name)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().startLocationName, name)
    }

    @Test
    fun testSetStageUiStateEndLocationName() = runTest {
        var name = "new location name"
        viewModel.addTrip()
        viewModel.setStageUiStateEndLocationName(0, 0, name)
        assertEquals(
            viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.first().endLocationName,
            name
        )
    }

    @Test
    fun testAddStageUiStateBefore() = runTest {
        viewModel.addTrip()
        viewModel.addStageUiStateBefore(0)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.size, 2)
    }

    @Test
    fun testAddStageUiStateAfter() = runTest {
        viewModel.addTrip()
        viewModel.addStageUiStateAfter(0)
        assertEquals(viewModel.tripsUiState.value.tripUiStates.first().stageUiStates.size, 2)
    }

}