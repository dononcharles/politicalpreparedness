package com.udacity.politicalpreparedness.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.RepresentativeResponse
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.data.network.models.dto.VoteInfoQueryDto
import com.udacity.politicalpreparedness.data.repo.ElectionDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
class RemoteDataSource(private val apiService: CivicsApiService, private val ioDispatcher: CoroutineDispatcher) : ElectionDataSource {

    private val _upcomingElections: MutableLiveData<Result<List<Election>>> = MutableLiveData()

    override suspend fun getElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            val result = try {
                Result.Success(apiService.getElections().elections)
            } catch (ex: Exception) {
                Result.Error(ex)
            }
            _upcomingElections.postValue(result)

            result
        }
    }

    override suspend fun saveElections(elections: List<Election>) {
        // no-op
    }

    override suspend fun deleteAllElections() {
        // no-op
    }

    override suspend fun getElectionDetails(address: String, electionId: Int): Result<State?> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(
                    apiService.getVoteInfo(
                        VoteInfoQueryDto(
                            address = address,
                            electionId = electionId,
                            officialOnly = false,
                            returnAllAvailableData = false,
                            productionDataOnly = false,
                        ),
                    ).state?.first(),
                )
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }

    override suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse> {
        return withContext(ioDispatcher) {
            try {
                Result.Success(apiService.getRepresentativesByAddress(address.toFormattedString()))
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }

    override fun observerElections(): LiveData<Result<List<Election>>> {
        return _upcomingElections
    }

    override suspend fun updateElection(election: Election) {
        // no-op
    }

    override suspend fun saveElection(election: Election) {
        // no-op
    }

    override suspend fun getElectionById(id: Int): Result<Election?> {
        // no-op
        return Result.Error(Exception("Not implemented"))
    }
}
