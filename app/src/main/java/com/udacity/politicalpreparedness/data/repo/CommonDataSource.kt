package com.udacity.politicalpreparedness.data.repo

import androidx.lifecycle.LiveData
import com.udacity.politicalpreparedness.data.network.models.Address
import com.udacity.politicalpreparedness.data.network.models.Election
import com.udacity.politicalpreparedness.data.network.models.State
import com.udacity.politicalpreparedness.data.network.models.dto.Result
import com.udacity.politicalpreparedness.ui.representative.model.Representative

/**
 * @author Komi Donon
 * @since 7/9/2023
 */
interface CommonDataSource {

    suspend fun getElections(forceUpdate: Boolean): Result<List<Election>>

    fun observeOnElections(): LiveData<Result<List<Election>>>

    suspend fun getElectionDetails(address: String, electionId: Int): Result<State?>

    suspend fun getRepresentatives(address: Address): Result<List<Representative>>

    suspend fun reloadElections()

    suspend fun updateElection(election: Election, isSaved: Boolean)
}
