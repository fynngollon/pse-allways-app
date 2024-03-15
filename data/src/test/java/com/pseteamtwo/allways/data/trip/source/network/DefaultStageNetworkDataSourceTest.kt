package com.pseteamtwo.allways.data.trip.source.network
/**
import com.pseteamtwo.allways.trip.Mode
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime
import java.sql.SQLException

class DefaultStageNetworkDataSourceTest {

    private lateinit var defaultStageNetworkDataSource: DefaultStageNetworkDataSource
    private lateinit var stage1: NetworkStage
    private lateinit var stage2: NetworkStage
    private lateinit var stages: List<NetworkStage>


    @Before
    fun setUp() {
        defaultStageNetworkDataSource = DefaultStageNetworkDataSource()
        stage1 = NetworkStage(
            123L,
            111L,
            Mode.CAR_DRIVER,
            LocalDateTime.of(2001,11,9,11,12),
            LocalDateTime.of(2001,11,9,11,40),
            GeoPoint(0.01,0.3),
            GeoPoint(1.23,32.5),
            12,
            23
        )
        stage2 = NetworkStage(
            122L,
            123L,
            Mode.CAR_DRIVER,
            LocalDateTime.of(2001,11,9,11,20),
            LocalDateTime.of(2001,11,9,11,40),
            GeoPoint(1.01,1.3),
            GeoPoint(1.23,32.5),
            12,
            23
        )
        stages = listOf(stage1, stage2)
    }
    @Test
    fun loadStages() {
        runBlocking {
            try {
                defaultStageNetworkDataSource.loadStages("kdb")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun saveStages() {
        runBlocking {
            try {
                defaultStageNetworkDataSource.saveStages("kdb", stages)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun deleteStage() {
        runBlocking {
            try {
                defaultStageNetworkDataSource.deleteStage("kdb", "123")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}
        */