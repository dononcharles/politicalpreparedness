package com.udacity.politicalpreparedness.ui.election

import android.app.Application
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.politicalpreparedness.FakeTestRepository
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.data.database.ElectionDatabase
import com.udacity.politicalpreparedness.data.database.LocalDataSource
import com.udacity.politicalpreparedness.data.network.CivicsApi
import com.udacity.politicalpreparedness.data.network.RemoteDataSource
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.data.repo.DefaultElectionsRepo
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.voteInfo.VoterInfoViewModel
import com.udacity.politicalpreparedness.ui.launch.LaunchViewModel
import com.udacity.politicalpreparedness.ui.representative.RepresentativeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/12/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ElectionsFragmentTest : KoinTest {

    private lateinit var appContext: Application
    private val repository = FakeTestRepository()

    @Before
    fun cleanUp() {
        runBlocking {
            repository.clearElections()
        }
    }

    @Before
    fun init() {
        stopKoin() // stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            single { ElectionDatabase.getInstance(appContext).electionDao }
            // Network
            single { CivicsApi.retrofitService }
            // Local Data Source
            single<ElectionDataSource>(qualifier = named("lo")) { LocalDataSource(get(), Dispatchers.IO) }
            // Remote Data Source
            single<ElectionDataSource>(qualifier = named("re")) { RemoteDataSource(get(), Dispatchers.IO) }
            // Repository
            single<CommonDataSource> {
                DefaultElectionsRepo(
                    get(qualifier = named("re")) as ElectionDataSource,
                    get(qualifier = named("lo")) as ElectionDataSource,
                    Dispatchers.IO,
                )
            }

            // ViewModels
            viewModel { LaunchViewModel() }
            viewModel { ElectionsViewModel(get()) }
            viewModel { RepresentativeViewModel(get()) }
            viewModel { (electionModel: ElectionModel) -> VoterInfoViewModel(get(), electionModel) }
        }
        // declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun upcomingElections_PopulateRecyclerView() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        val election1 = Election(1, "election1", Date(1689109003), Division("id1", "us", "ca"), false)
        val election2 = Election(2, "election2", Date(1689109003), Division("id2", "us", "ca"), false)
        val election3 = Election(3, "election3", Date(1689109003), Division("id3", "us", "ca"), false)

        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        advanceUntilIdle()

        onView(withId(R.id.upcomingElectionsRv)).check { view, _ -> assertNotNull(view) }
        onView(withId(R.id.upcomingElectionsRv)).check(matches(hasMinimumChildCount(1)))
        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(isDisplayed()))
        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(withText(R.string.upcoming_elections)))
        onView(withId(R.id.savedElectionsHeaderTv)).check(matches(isDisplayed()))
        onView(withId(R.id.savedElectionsRv)).check(matches(isDisplayed()))
        onView(withId(R.id.savedElectionsRv)).check(matches(hasChildCount(0)))
    }

    @Test
    fun upcomingElections_DisplaySavedElections() = runTest {
        val election1 = Election(1, "election1", Date(1689109003), Division("id1", "us", "ca"), true)
        val election2 = Election(2, "election2", Date(1689109003), Division("id2", "us", "ca"), false)
        val election3 = Election(3, "election3", Date(1689109003), Division("id3", "us", "ca"), true)

        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.upcomingElectionsRv)).check(matches(isDisplayed()))

        onView(withId(R.id.upcomingElectionsRv)).check(matches(hasMinimumChildCount(1)))
        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(isDisplayed()))
        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(withText(R.string.upcoming_elections)))
        onView(withId(R.id.savedElectionsHeaderTv)).check(matches(isDisplayed()))
        onView(withId(R.id.savedElectionsHeaderTv)).check(matches(withText(R.string.saved_elections)))
        onView(withId(R.id.savedElectionsRv)).check(matches(isDisplayed()))
    }
}
