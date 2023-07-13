package com.udacity.politicalpreparedness.data.repo

import com.udacity.politicalpreparedness.MainDispatcherRule
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsInstanceOf
import org.hamcrest.core.IsNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/12/2023
 */

@ExperimentalCoroutinesApi
class DefaultElectionsRepoTest {

    private val election1 = Election(1, "election1", Date(1689109003), Division("id1", "us", "ca"), false)
    private val election2 = Election(2, "election2", Date(1689109003), Division("id2", "us", "ca"), false)
    private val election3 = Election(3, "election3", Date(1689109003), Division("id3", "us", "ca"), false)

    private val remoteElections = listOf(election1, election2, election3)
    private val localElections = listOf<Election>()

    private lateinit var electionsRemoteDataSource: FakeDataSource
    private lateinit var electionsLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var electionsRepository: DefaultElectionsRepo

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @Before
    fun createRepository() {
        electionsRemoteDataSource = FakeDataSource(remoteElections.toMutableList())
        electionsLocalDataSource = FakeDataSource(localElections.toMutableList())
        // Get a reference to the class under test
        electionsRepository = DefaultElectionsRepo(
            electionsRemoteDataSource,
            electionsLocalDataSource,
            Dispatchers.Main,
        )
    }

    @Test
    fun getElections_requestsElectionsFromRemoteDataSource() = runTest {
        // When tasks are requested from the tasks repository
        val result = electionsRepository.getElections(true) as Result.Success

        // Then elections are loaded from the remote data source
        assertThat(result.data, IsEqual(remoteElections))
    }

    @Test
    fun getElections_requestsElectionsFromLocalDataSource() = runTest {
        // When tasks are requested from the tasks repository
        val result = electionsRepository.getElections(true) as Result.Success

        // Then elections are loaded from the remote data source
        assertThat(result.data, IsEqual(remoteElections))
    }

    @Test
    fun getRepresentatives_returnSuccess() = runTest {
        val address = Address("123 Main St", "CA", "Los Angeles", "Ca", "90002")

        val r = electionsRepository.getRepresentatives(address)

        assertThat(r, IsInstanceOf(Result.Success::class.java))
        assertThat(r is Result.Success, IsEqual(true))
        assertThat((r as Result.Success).data.size, not(IsNull()))
    }

    @Test
    fun getRepresentatives_returnError() = runTest {
        electionsRemoteDataSource.returnError(true)
        val address = Address("123 Main St", "CA", "Los Angeles", "Ca", "90001")

        val r = electionsRepository.getRepresentatives(address)

        assertThat(r is Result.Error, IsEqual(true))
    }

    @Test
    fun getElectionsDetails_returnError() = runTest {
        electionsRemoteDataSource.returnError(true)

        val r = electionsRepository.getElectionDetails("address", 1)

        assertThat(r is Result.Error, IsEqual(true))
    }

    @Test
    fun getElectionsDetails_returnSuccess() = runTest {
        val r = electionsRepository.getElectionDetails("address", 1)

        assertThat(r is Result.Success, IsEqual(true))
    }
}
