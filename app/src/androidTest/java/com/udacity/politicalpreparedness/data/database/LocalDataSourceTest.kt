package com.udacity.politicalpreparedness.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var database: ElectionDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ElectionDatabase::class.java)
            .allowMainThreadQueries().build()

        localDataSource = LocalDataSource(database.electionDao, Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveElection_getElection() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = false)
        localDataSource.saveElection(election)

        // WHEN - Get the election by id from the database.
        val result = localDataSource.getElectionById(election.id)

        // THEN - The loaded data contains the expected values.
        assertThat(result, `is`(Result.Success(election)))
        result as Result.Success
        assertThat(result.data?.id, `is`(5217))
        assertThat(result.data?.name, `is`("VIP Test Election"))
        assertThat(result.data?.electionDay, `is`(Date(1689109003)))
        assertThat(result.data?.division?.id, `is`("ocd-division/country:us"))
    }

    @Test
    fun saveElection_getElectionByIdAndUpdate() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = false)
        localDataSource.saveElection(election)

        // WHEN - Get the election by id from the database.
        val result = localDataSource.getElectionById(election.id)

        // THEN - The loaded data contains the expected values.
        assertThat(result, `is`(Result.Success(election)))
        result as Result.Success
        assertThat(result.data?.id, `is`(5217))
        assertThat(result.data?.name, `is`("VIP Test Election"))
        assertThat(result.data?.electionDay, `is`(Date(1689109003)))
        assertThat(result.data?.division?.id, `is`("ocd-division/country:us"))

        // WHEN - Update the election
        val updatedElection = election.copy(isSaved = true)
        localDataSource.updateElection(updatedElection)

        // THEN - The loaded data contains the expected values.
        val updatedResult = localDataSource.getElectionById(election.id)
        assertThat(updatedResult, `is`(Result.Success(updatedElection)))
        updatedResult as Result.Success
        assertThat(updatedResult.data?.id, `is`(5217))
        assertThat(updatedResult.data?.name, `is`("VIP Test Election"))
        assertThat(updatedResult.data?.electionDay, `is`(Date(1689109003)))
        assertThat(updatedResult.data?.division?.id, `is`("ocd-division/country:us"))
        assertThat(updatedResult.data?.isSaved, `is`(true))
    }
}
