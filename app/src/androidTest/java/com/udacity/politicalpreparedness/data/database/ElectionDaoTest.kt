package com.udacity.politicalpreparedness.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
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
@SmallTest
class ElectionDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ElectionDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ElectionDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertElection_getElectionById() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = false)

        database.electionDao.insert(election)

        // WHEN - Get the election by id from the database.
        val loaded = database.electionDao.getById(election.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded as Election, notNullValue())
        assertThat(loaded.id, `is`(election.id))
        assertThat(loaded.name, `is`(election.name))
        assertThat(loaded.electionDay, `is`(election.electionDay))
    }

    @Test
    fun insertElection_getAllElections() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = false)

        database.electionDao.insert(election)

        // WHEN - Get the election by id from the database.
        val loaded = database.electionDao.getAll()

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, notNullValue())
        assertThat(loaded[0].id, `is`(election.id))
        assertThat(loaded[0].name, `is`(election.name))
        assertThat(loaded[0].electionDay, `is`(election.electionDay))
    }

    @Test
    fun insertElection_deleteElection() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = false)

        database.electionDao.insert(election)

        // WHEN - Get the election by id from the database.
        database.electionDao.deleteById(election.id)

        val loaded = database.electionDao.getById(election.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded, `is`(nullValue()))
    }

    @Test
    fun insertElection_returnIsSaved() = runTest {
        // GIVEN - Insert an election.
        val election = Election(id = 5217, name = "VIP Test Election", electionDay = Date(1689109003), division = Division(id = "ocd-division/country:us", country = "us", state = "ok"), isSaved = true)

        database.electionDao.insert(election)

        // WHEN - Get the election by id from the database.
        val loaded = database.electionDao.getById(election.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded as Election, notNullValue())
        assertThat(loaded.isSaved, `is`(true))
    }
}
