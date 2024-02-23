package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.account.source.network.NetworkAccount
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.sql.SQLException

class DefaultStageNetworkDataSourceTest {

    private lateinit var defaultStageNetworkDataSource: DefaultStageNetworkDataSource

    @Before
    fun setUp() {
        defaultStageNetworkDataSource = DefaultStageNetworkDataSource()
    }
    @Test
    fun loadStages() {
    }

    @Test
    fun saveStages() {
    }

    @Test
    fun deleteStage() {
        runBlocking {
            try {
                defaultStageNetworkDataSource.deleteStage("kdb", "lol")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}