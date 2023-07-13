package com.udacity.politicalpreparedness.ui.election

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavDirections
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.politicalpreparedness.MainDispatcherRule
import com.udacity.politicalpreparedness.base.NavigationCommand
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.repo.FakeTestRepository
import com.udacity.politicalpreparedness.getOrAwaitValue
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.adapter.toElection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ElectionsViewModelTest {

    // Use a fake repository to be injected into the viewModel
    private lateinit var electionsRepository: FakeTestRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var electionsViewModel: ElectionsViewModel

    @Before
    fun setupViewModel() {
        stopKoin()
        electionsRepository = FakeTestRepository()
        electionsViewModel = ElectionsViewModel(electionsRepository)
        val election1 = Election(21, "Title_1", Date(1689109003000), Division("31", "us", "ca"))
        val election2 = Election(22, "Title_2", Date(1689109003000), Division("32", "us", "io"))
        val election3 = Election(23, "Title_3", Date(1689109003000), Division("33", "us", "fl"))
        electionsRepository.addElections(election1, election2, election3)
    }

    @After
    fun clearData() = runTest {
        electionsRepository.clearElections()
    }

    @Test
    fun reload_ShowLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        electionsViewModel.refreshElectionsList()

        assertThat(electionsViewModel.showLoading.getOrAwaitValue(), `is`(true))

        advanceUntilIdle()

        assertThat(electionsViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun reload_UpcomingAndSavedElections() {
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), false)

        electionsRepository.addElections(electionModel.toElection().copy(isSaved = true))
        electionsViewModel.refreshElectionsList()

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue()?.isEmpty(), `is`(false))

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue(), `is`(notNullValue()))
        assertThat(electionsViewModel.electionSaved.getOrAwaitValue()?.size, `is`(1))
    }

    @Test
    fun upcomingElection_ItemSelectedNavigateToInfoView() {
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), false)
        electionsViewModel.onUpComingItemClicked(electionModel)

        assertThat((electionsViewModel.navigationCommand.getOrAwaitValue() as NavigationCommand.To).directions, `is`(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(electionModel)))
    }

    @Test
    fun savedElection_ItemSelectedNavigateToInfoView() {
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), true)
        electionsViewModel.onSavedItemClicked(electionModel)

        assertThat((electionsViewModel.navigationCommand.getOrAwaitValue() as NavigationCommand.To).directions, IsInstanceOf(ElectionsFragmentDirections.ActionElectionsFragmentToVoterInfoFragment::class.java))
    }
}
