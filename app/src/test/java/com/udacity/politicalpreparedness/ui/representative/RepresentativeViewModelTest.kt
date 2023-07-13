package com.udacity.politicalpreparedness.ui.representative

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.politicalpreparedness.MainDispatcherRule
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.data.repo.FakeTestRepository
import com.udacity.politicalpreparedness.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RepresentativeViewModelTest {
    // Subject under test
    private lateinit var representativeViewModel: RepresentativeViewModel

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
        electionsRepository = FakeTestRepository()
        representativeViewModel = RepresentativeViewModel(electionsRepository)
    }

    @Test
    fun findMyRepresentative_ErrorWithEmptyLine1() {
        representativeViewModel.findMyRepresentatives()

        assertThat(representativeViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.error_missing_first_line_address))
    }

    @Test
    fun findMyRepresentative_ErrorWithEmptyCity() {
        representativeViewModel.line1.value = "1234 Main St"
        representativeViewModel.findMyRepresentatives()

        assertThat(representativeViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.error_missing_city))
    }

    @Test
    fun findMyRepresentative_ErrorWithEmptyState() {
        representativeViewModel.line1.value = "1234 Main St"
        representativeViewModel.city.value = "New York"

        representativeViewModel.findMyRepresentatives()

        assertThat(representativeViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.error_missing_state))
    }

    @Test
    fun findMyRepresentative_ErrorWithEmptyZip() {
        representativeViewModel.line1.value = "1234 Main St"
        representativeViewModel.city.value = "New York"
        representativeViewModel.state.value = "NY"

        representativeViewModel.findMyRepresentatives()

        assertThat(representativeViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.error_missing_zip))
    }

    @Test
    fun findMyRepresentative_ByEnteringAddressAndReturnSuccess() {
        representativeViewModel.line1.value = "1234 Main St"
        representativeViewModel.city.value = "New York"
        representativeViewModel.state.value = "NY"
        representativeViewModel.zip.value = "10001"

        representativeViewModel.findMyRepresentatives()

        assertThat(representativeViewModel.representatives.getOrAwaitValue().size, `is`(notNullValue()))
    }
}
