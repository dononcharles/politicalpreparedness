package com.udacity.politicalpreparedness.ui.representative

import android.app.Application
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import com.udacity.politicalpreparedness.data.database.ElectionDatabase
import com.udacity.politicalpreparedness.data.database.LocalDataSource
import com.udacity.politicalpreparedness.data.network.CivicsApi
import com.udacity.politicalpreparedness.data.network.RemoteDataSource
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.data.repo.DefaultElectionsRepo
import com.udacity.politicalpreparedness.ui.election.ElectionsViewModel
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.voteInfo.VoterInfoViewModel
import com.udacity.politicalpreparedness.ui.launch.LaunchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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

/**
 * @author Komi Donon
 * @since 7/12/2023
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class RepresentativeFragmentTest : KoinTest {

    private lateinit var appContext: Application

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
    fun findRepresentative_ReturnErrorMissingLine1() = runTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)
        onView(withId(R.id.button_search)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.error_missing_first_line_address)))
    }

    @Test
    fun findRepresentative_ReturnErrorMissingCity() = runTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)
        onView(withId(R.id.address_line_1)).perform(typeText("Mountain View"))
        closeSoftKeyboard()
        onView(withId(R.id.button_search)).perform(click())
        onView(withText(R.string.error_missing_city)).check(matches(isDisplayed()))
    }

    @Test
    fun findRepresentative_ReturnErrorMissingZip() = runTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)
        onView(withId(R.id.address_line_1)).perform(typeText("Road Name"))
        onView(withId(R.id.city)).perform(typeText("Iowa City"))
        closeSoftKeyboard()
        onView(withId(R.id.button_search)).perform(click())
        onView(withText(R.string.error_missing_zip)).check(matches(isDisplayed()))
    }
}
