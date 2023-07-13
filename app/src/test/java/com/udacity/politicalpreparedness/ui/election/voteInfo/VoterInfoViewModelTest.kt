package com.udacity.politicalpreparedness.ui.election.voteInfo

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.politicalpreparedness.MainDispatcherRule
import com.udacity.politicalpreparedness.base.NavigationCommand
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.repo.FakeTestRepository
import com.udacity.politicalpreparedness.getOrAwaitValue
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.hamcrest.core.IsNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import java.util.Date
import java.util.Locale

/**
 * @author Komi Donon
 * @since 7/12/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class VoterInfoViewModelTest {
    // Subject under test
    private lateinit var voterInfoViewModel: VoterInfoViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var electionsRepository: FakeTestRepository

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        GlobalContext.stopKoin()
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), false)

        electionsRepository = FakeTestRepository()
        voterInfoViewModel = VoterInfoViewModel(electionsRepository, electionModel)
    }

    @Test
    fun loadElectionDetails_ReturnSuccessResponse() {
        val address = Address(Locale.getDefault())
        voterInfoViewModel.loadDetailsByAddress(address)

        assertThat(voterInfoViewModel.voterInfo.getOrAwaitValue(), not(IsNull()))
        assertThat(voterInfoViewModel.voterInfo.getOrAwaitValue(), IsInstanceOf(State::class.java))
    }

    @Test
    fun onSavedButtonClicked_ActionFollowElection() = runTest {
        voterInfoViewModel.electionModel.isSaved = true
        voterInfoViewModel.onSavedBtnClicked()

        assertThat((voterInfoViewModel.navigationCommand.getOrAwaitValue() as NavigationCommand.Back), `is`(notNullValue()))
    }
}
