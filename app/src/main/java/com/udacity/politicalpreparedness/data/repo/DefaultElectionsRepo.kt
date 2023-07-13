package com.udacity.politicalpreparedness.data.repo

import android.util.Log
import com.udacity.politicalpreparedness.data.network.mapErrorResponse
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.ui.representative.model.Representative
import com.udacity.politicalpreparedness.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
class DefaultElectionsRepo(private val remoteDataSourceRepo: ElectionDataSource, private val localDataSource: ElectionDataSource, private val ioDispatcher: CoroutineDispatcher) : CommonDataSource {

    override suspend fun getElections(forceUpdate: Boolean): Result<List<Election>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateElectionsFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return localDataSource.getElections()
        }
    }

    override suspend fun getElectionDetails(address: String, electionId: Int): Result<State?> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                remoteDataSourceRepo.getElectionDetails(address, electionId)
            }
        }
    }

    override suspend fun getRepresentatives(address: Address): Result<List<Representative>> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                when (val response = remoteDataSourceRepo.getRepresentatives(address)) {
                    is Result.Error -> Result.Error(response.exception.mapErrorResponse())
                    is Result.Success -> {
                        val officials = response.data.officials
                        val representatives = response.data.offices.flatMap { it.getRepresentatives(officials) }
                        Result.Success(representatives)
                    }

                    is Result.Loading -> Result.Loading()
                }
            }
        }
    }

    override suspend fun reloadElections() {
        wrapEspressoIdlingResource {
            updateElectionsFromRemoteDataSource()
        }
    }

    override suspend fun updateElection(election: Election, isSaved: Boolean) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    localDataSource.updateElection(election.copy(isSaved = isSaved))
                }
                // Log.e("VoterInfoViewModel:onSavedBtnClicked: %s", isSaved.toString())
                // localDataSource.updateElection(election.copy(isSaved = isSaved))
            }
        }
    }

    /*override suspend fun removeElection(election: Election) {
        withContext(ioDispatcher) {
            localDataSource.deleteElection(election.id)
        }
    }*/

    override fun observeOnElections() = wrapEspressoIdlingResource {
        localDataSource.observerElections()
    }

    private suspend fun updateElectionsFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            try {
                val elections = remoteDataSourceRepo.getElections()
                if (elections is Result.Success) {
                    val dbElections = localDataSource.getElections()
                    Log.e("DefaultElectionsRepo: %s", dbElections.toString())
                    localDataSource.deleteAllElections()
                    // localDataSource.saveElections(elections.data)
                    if (dbElections is Result.Success) {
                        val saved = dbElections.data.filter { it.isSaved }
                        elections.data.map { elec ->
                            elec.copy(isSaved = saved.firstOrNull { it.id == elec.id }?.isSaved == true)
                        }
                    } else {
                        elections.data
                    }.run {
                        Log.e("DefaultElectionsRepo: %s", this.toString())
                        localDataSource.saveElections(this)
                    }
                } else if (elections is Result.Error) {
                    throw elections.exception
                }
            } catch (ex: Exception) {
                throw ex
            }
        }
    }
}
