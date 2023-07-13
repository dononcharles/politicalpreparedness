package com.udacity.politicalpreparedness

import android.app.Application
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import com.udacity.politicalpreparedness.data.database.ElectionDatabase
import com.udacity.politicalpreparedness.data.database.LocalDataSource
import com.udacity.politicalpreparedness.data.network.CivicsApi
import com.udacity.politicalpreparedness.data.network.RemoteDataSource
import com.udacity.politicalpreparedness.data.network.models.Division
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.data.repo.DefaultElectionsRepo
import com.udacity.politicalpreparedness.ui.MainActivity
import com.udacity.politicalpreparedness.ui.election.ElectionsViewModel
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionViewHolder
import com.udacity.politicalpreparedness.ui.election.voteInfo.VoterInfoViewModel
import com.udacity.politicalpreparedness.ui.launch.LaunchViewModel
import com.udacity.politicalpreparedness.ui.representative.RepresentativeViewModel
import com.udacity.politicalpreparedness.util.DataBindingIdlingResource
import com.udacity.politicalpreparedness.util.monitorActivity
import com.udacity.politicalpreparedness.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.After
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
import java.util.Date

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : KoinTest {

    // An idle resource that waits for all outstanding data bindings to be completed
    private val dataBindingResource = DataBindingIdlingResource()
    private lateinit var appContext: Application
    private val repository = FakeTestRepository()

    private var decorView: View? = null

    @JvmField
    @Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity -> decorView = activity.window.decorView }
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

    // In order to be garbage collected and prevent memory leaks, deregister your idle resource.
    @After
    fun unregisterResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingResource)
    }

    @Before
    fun registerResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingResource)
    }

    @Rule
    @JvmField
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun upcomingElections_followAndUnfollowElection() = runTest {
        // GIVEN - Populate database
        val election1 = Election(id = 555, name = "TITLE1", electionDay = Date(1689109003), division = Division(id = "11", country = "us", state = "ca"), isSaved = false)
        val election2 = Election(id = 556, name = "TITLE2", electionDay = Date(1689109003), division = Division(id = "12", country = "us", state = "la"), isSaved = false)
        val election3 = Election(id = 557, name = "TITLE3", electionDay = Date(1689109003), division = Division(id = "13", country = "us", state = "lo"), isSaved = false)
        repository.addElections(election1, election2, election3)

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingResource.monitorActivity(scenario)

        onView(withId(R.id.upcomingElectionsBtn)).perform(click())

        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(isDisplayed()))
        onView(withId(R.id.upcomingElectionsHeaderTv)).check(matches(withText(R.string.upcoming_elections)))
        onView(withId(R.id.savedElectionsHeaderTv)).check(matches(isDisplayed()))

        onView(withId(R.id.upcomingElectionsRv)).perform(RecyclerViewActions.actionOnItemAtPosition<ElectionViewHolder>(0, click()))

        onView(withId(R.id.follow_election_button)).check(matches(withText(R.string.voter_add_to_saved)))
        onView(withId(R.id.follow_election_button)).perform(click())

        onView(withId(R.id.savedElectionsHeaderTv)).check(matches(withText(R.string.saved_elections)))
        onView(withId(R.id.savedElectionsRv)).check(matches(isDisplayed()))
        onView(withId(R.id.savedElectionsRv)).check(matches(hasChildCount(1)))

        onView(withId(R.id.savedElectionsRv)).perform(RecyclerViewActions.actionOnItemAtPosition<ElectionViewHolder>(0, click()))
        onView(withId(R.id.follow_election_button)).check(matches(withText(R.string.voter_remove_from_saved)))
        onView(withId(R.id.follow_election_button)).perform(click())

        scenario.close()
    }

    @Test
    fun myRepresentative_searchForMyRepresentative() = runTest {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingResource.monitorActivity(scenario)

        onView(withId(R.id.findRepresentativesButton)).perform(click())

        onView(withId(R.id.address_line_1)).perform(typeText("1600 Amphitheatre Parkway"))
        onView(withId(R.id.city)).perform(typeText("Mountain View"))

        onView(withId(R.id.state)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)))).atPosition(5).perform(click())
        // onData(allOf(`is`(instanceOf(String::class.java)), `is`("California"))).perform(click())

        onView(withId(R.id.zip)).perform(typeText("94043"))

        onView(withId(R.id.button_search)).perform(click())

        onView(withId(R.id.representativeRc)).check(matches(isDisplayed()))
        onView(withId(R.id.representativeRc)).check(matches(hasMinimumChildCount(1)))

        scenario.close()
    }
}
