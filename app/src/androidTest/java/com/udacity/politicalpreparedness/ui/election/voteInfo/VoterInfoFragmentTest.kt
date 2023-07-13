package com.udacity.politicalpreparedness.ui.election.voteInfo

import android.app.Application
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import com.udacity.politicalpreparedness.data.database.ElectionDatabase
import com.udacity.politicalpreparedness.data.database.LocalDataSource
import com.udacity.politicalpreparedness.data.network.CivicsApi
import com.udacity.politicalpreparedness.data.network.RemoteDataSource
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.data.repo.DefaultElectionsRepo
import com.udacity.politicalpreparedness.ui.election.ElectionsViewModel
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.launch.LaunchViewModel
import com.udacity.politicalpreparedness.ui.representative.RepresentativeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/12/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class VoterInfoFragmentTest : KoinTest {
    private lateinit var appContext: Application

    @Rule
    @JvmField
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

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
    fun followElection_AndNavigateBackToElectionsUI() = runTest {
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), false)

        val extras = bundleOf("arg_election" to electionModel)
        val scenario = launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.follow_election_button)).check(matches(withText(R.string.voter_add_to_saved)))
        onView(withId(R.id.follow_election_button)).perform(click())

        // verify(navController).navigateUp()
    }

    @Test
    fun unfollowElection_AndNavigateBackToElectionsUI() = runTest {
        val electionModel = ElectionModel(1, "Title_1", Date(1689109003000), Division("1", "us", "ca"), true)

        val extras = bundleOf("arg_election" to electionModel)
        val scenario = launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.follow_election_button)).check(matches(withText(R.string.voter_remove_from_saved)))
        onView(withId(R.id.follow_election_button)).perform(click())

        // verify(navController).popBackStack()
    }
}
