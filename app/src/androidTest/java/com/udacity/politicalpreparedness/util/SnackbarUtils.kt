package com.udacity.politicalpreparedness.util

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.material.snackbar.Snackbar
import org.hamcrest.Matcher
/**
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

object SnackbarUtils {
    /**
     * Helper method that shows that specified [Snackbar] and waits until
     * it has been fully shown. Note that calling this method will reset the currently
     * set [Snackbar.Callback].
     */
    fun showSnackbarAndWaitUntilFullyShown(snackbar: Snackbar) {
        val snackbarCallback = SnackbarShownCallback()
        snackbar.addCallback(snackbarCallback)
        try {
            // Register our listener as idling resource so that Espresso waits until the
            // the snackbar has been fully shown
            IdlingRegistry.getInstance().register(snackbarCallback)
            // Show the snackbar
            snackbar.show()
            // Mark the callback to require waiting for idle state
            snackbarCallback.mNeedsIdle = true
            // Perform a dummy Espresso action that loops until the UI thread is idle. This
            // effectively blocks us until the Snackbar has completed its sliding animation.
            Espresso.onView(ViewMatchers.isRoot()).perform(waitUntilIdle())
            snackbarCallback.mNeedsIdle = false
        } finally {
            // Unregister our idling resource
            IdlingRegistry.getInstance().unregister(snackbarCallback)
            // And remove our tracker listener from Snackbar
            snackbar.addCallback(null)
        }
    }

    /**
     * Helper method that dismissed that specified [Snackbar] and waits until
     * it has been fully dismissed. Note that calling this method will reset the currently
     * set [Snackbar.Callback].
     */
    fun dismissSnackbarAndWaitUntilFullyDismissed(snackbar: Snackbar) {
        val snackbarCallback = SnackbarDismissedCallback()
        snackbar.addCallback(snackbarCallback)
        try {
            // Register our listener as idling resource so that Espresso waits until the
            // the snackbar has been fully dismissed
            IdlingRegistry.getInstance().register(snackbarCallback)
            // Dismiss the snackbar
            snackbar.dismiss()
            // Mark the callback to require waiting for idle state
            snackbarCallback.mNeedsIdle = true
            // Perform a dummy Espresso action that loops until the UI thread is idle. This
            // effectively blocks us until the Snackbar has completed its sliding animation.
            Espresso.onView(ViewMatchers.isRoot()).perform(waitUntilIdle())
            snackbarCallback.mNeedsIdle = false
        } finally {
            // Unregister our idling resource
            IdlingRegistry.getInstance().unregister(snackbarCallback)
            // And remove our tracker listener from Snackbar
            snackbar.addCallback(null)
        }
    }

    /**
     * Dummy Espresso action that waits until the UI thread is idle. This action can be performed
     * on the root view to wait for an ongoing animation to be completed.
     */
    private fun waitUntilIdle(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "wait for idle"
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}

private class SnackbarShownCallback : Snackbar.Callback(), IdlingResource {
    private var mIsShown = false
    private var mCallback: IdlingResource.ResourceCallback? = null
    var mNeedsIdle = false

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        mCallback = resourceCallback
    }

    override fun getName(): String {
        return "Snackbar shown callback"
    }

    override fun isIdleNow(): Boolean {
        return if (!mNeedsIdle) {
            true
        } else {
            mIsShown
        }
    }

    override fun onShown(snackbar: Snackbar) {
        mIsShown = true
        if (mCallback != null) {
            mCallback!!.onTransitionToIdle()
        }
    }
}

private class SnackbarDismissedCallback : Snackbar.Callback(), IdlingResource {

    private var mIsDismissed = false
    private var mCallback: IdlingResource.ResourceCallback? = null
    var mNeedsIdle = false

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        mCallback = resourceCallback
    }

    override fun getName(): String {
        return "Snackbar dismissed callback"
    }

    override fun isIdleNow(): Boolean {
        return if (!mNeedsIdle) {
            true
        } else {
            mIsDismissed
        }
    }

    override fun onDismissed(snackbar: Snackbar, @DismissEvent event: Int) {
        mIsDismissed = true
        if (mCallback != null) {
            mCallback!!.onTransitionToIdle()
        }
    }
}
