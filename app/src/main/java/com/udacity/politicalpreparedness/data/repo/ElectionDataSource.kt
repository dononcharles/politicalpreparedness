package com.udacity.politicalpreparedness.data.repo

import androidx.lifecycle.LiveData
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.RepresentativeResponse
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
interface ElectionDataSource {

    suspend fun getElections(): Result<List<Election>>

    suspend fun saveElections(elections: List<Election>)

    suspend fun deleteAllElections()

    suspend fun getElectionDetails(address: String, electionId: Int): Result<State?>

    suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse>

    fun observerElections(): LiveData<Result<List<Election>>>

    suspend fun updateElection(election: Election)

    suspend fun saveElection(election: Election)

    suspend fun getElectionById(id: Int): Result<Election?>
}
