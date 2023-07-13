package com.udacity.politicalpreparedness.ui.launch

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.politicalpreparedness.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito

/**
 * @author Komi Donon
 * @since 7/11/2023
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class LaunchFragmentTest : KoinTest {

    @Before
    fun init() {
        stopKoin()
        val myModule = module {
            viewModel { LaunchViewModel() }
        }
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
    }

    // Click on Upcoming Elections button and navigate to ElectionsFragment.
    @Test
    fun clickUpcomingElectionsButton_navigateToElectionsFragment() = runTest {
        // Given - On the Launch ui .
        val scenario = launchFragmentInContainer<LaunchFragment>(null, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // When - Click on the Btn.
        Espresso.onView(withId(R.id.upcomingElectionsBtn)).perform(click())

        // Then - Verify that we navigate to election screen.
        Mockito.verify(navController).navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    // Click on Find My Representatives button and navigate to RepresentativeFragment.
    @Test
    fun clickFindMyRepresentativesButton_navigateToRepresentativeFragment() = runTest {
        // Given - On the Launch ui .
        val scenario = launchFragmentInContainer<LaunchFragment>(null, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // When - Click on the Btn.
        Espresso.onView(withId(R.id.findRepresentativesButton)).perform(ViewActions.click())

        // Then - Verify that we navigate to election screen.
        Mockito.verify(navController).navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
