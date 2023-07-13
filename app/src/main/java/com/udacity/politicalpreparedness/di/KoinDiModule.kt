package com.udacity.politicalpreparedness.di

import com.udacity.politicalpreparedness.data.database.ElectionDatabase
import com.udacity.politicalpreparedness.data.database.LocalDataSource
import com.udacity.politicalpreparedness.data.network.CivicsApi
import com.udacity.politicalpreparedness.data.network.RemoteDataSource
import com.udacity.politicalpreparedness.data.repo.CommonDataSource
import com.udacity.politicalpreparedness.data.repo.DefaultElectionsRepo
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import com.udacity.politicalpreparedness.ui.election.ElectionsViewModel
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel
import com.udacity.politicalpreparedness.ui.election.voteInfo.VoterInfoViewModel
import com.udacity.politicalpreparedness.ui.launch.LaunchViewModel
import com.udacity.politicalpreparedness.ui.representative.RepresentativeViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * @author Komi Donon
 * @since 7/9/2023
 */

val koinDiModule = module {
    // Database
    single { ElectionDatabase.getInstance(androidContext()).electionDao }
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
