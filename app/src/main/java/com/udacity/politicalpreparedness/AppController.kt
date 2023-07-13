package com.udacity.politicalpreparedness

import android.app.Application
import com.udacity.politicalpreparedness.di.koinDiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppController)
            modules(koinDiModule)
        }
    }
}
