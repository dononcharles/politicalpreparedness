package com.udacity.politicalpreparedness.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.RepresentativeResponse
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
class LocalDataSource(private val electionDao: ElectionDao, private val ioDispatcher: CoroutineDispatcher) : ElectionDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(electionDao.getAll())
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }

    /* override suspend fun saveElection(election: Election) {
         withContext(ioDispatcher) {
             electionDao.insert(election)
         }
     }
 */
    override suspend fun saveElections(elections: List<Election>) {
        Log.e("LocalDataSource:saveElections: %s", elections.toString())
        withContext(ioDispatcher) {
            elections.map { it.copy(isSaved = false) }
            electionDao.insertAll(elections)
        }
    }

    /*override suspend fun deleteElection(id: Int) {
        withContext(ioDispatcher) {
            electionDao.deleteById(id)
        }
    }*/

    override suspend fun deleteAllElections() {
        withContext(ioDispatcher) {
            electionDao.clearAll()
        }
    }

    override suspend fun getElectionDetails(address: String, electionId: Int): Result<State?> {
        return Result.Error(Exception("Not implemented"))
    }

    override suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse> {
        // Not required for local data source
        return Result.Error(Exception("Not implemented"))
    }

    override fun observerElections(): LiveData<Result<List<Election>>> {
        return electionDao.getAllLiveData().map { Result.Success(it) }
    }

    override suspend fun updateElection(election: Election) {
        withContext(ioDispatcher) {
            Log.e("VoterInfoViewModel:onSavedBtnClicked: %s", "isSaved.toString()")
            // todo electionDao.updateElection(election.id, election.division.id, election.isSaved)
            electionDao.insert(election)
        }
    }

    override suspend fun saveElection(election: Election) {
        withContext(ioDispatcher) {
            Log.e("VoterInfoViewModel:onSavedBtnClicked: %s", "isSaved.toString()")
            // todo electionDao.updateElection(election.id, election.division.id, election.isSaved)
            electionDao.insert(election)
        }
    }

    override suspend fun getElectionById(id: Int): Result<Election?> {
        return withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(electionDao.getById(id))
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }
}
